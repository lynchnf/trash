package norman.trash.controller;

import norman.trash.NotFoundException;
import norman.trash.OptimisticLockingException;
import norman.trash.controller.view.DataFileListForm;
import norman.trash.controller.view.DataFileView;
import norman.trash.domain.DataFile;
import norman.trash.domain.DataFileStatus;
import norman.trash.domain.DataLine;
import norman.trash.service.DataFileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;

import static norman.trash.MessagesConstants.*;

@Controller
public class DataFileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataFileController.class);
    private static final String defaultSortColumn = "id";
    private static final String[] dataFileSortableColumns =
            {"originalFilename", "contentType", "uploadTimestamp", "status"};
    @Autowired
    private DataFileService dataFileService;

    @GetMapping("/dataFileList")
    // @formatter:off
    public String loadDataFileList( @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "sortColumn", required = false, defaultValue = "uploadTimestamp") String sortColumn,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Model model,
            @RequestParam(value = "whereOriginalFilename", required = false) String whereOriginalFilename,
            @RequestParam(value = "whereStatus", required = false) DataFileStatus whereStatus) {
        // @formatter:on
        String trimmedOriginalFilename = StringUtils.trimToNull(whereOriginalFilename);

        // Convert sort column from string to an array of strings.
        String[] sortColumns = {defaultSortColumn};
        if (Arrays.asList(dataFileSortableColumns).contains(sortColumn)) {
            sortColumns = new String[]{sortColumn, defaultSortColumn};
        }

        // Get a page of records.
        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sortDirection, sortColumns);
        Page<DataFile> page = null;

        if (trimmedOriginalFilename != null && whereStatus != null) {
            page = dataFileService.findByOriginalFilenameAndStatus(trimmedOriginalFilename, whereStatus, pageable);
        } else if (trimmedOriginalFilename != null && whereStatus == null) {
            page = dataFileService.findByOriginalFilename(trimmedOriginalFilename, pageable);
        } else if (trimmedOriginalFilename == null && whereStatus != null) {
            page = dataFileService.findByStatus(whereStatus, pageable);
        } else {
            page = dataFileService.findAll(pageable);
        }
        DataFileListForm dataFileListForm = new DataFileListForm(page, whereOriginalFilename, whereStatus);
        model.addAttribute("listForm", dataFileListForm);
        return "dataFileList";
    }

    @GetMapping("/dataFile")
    // @formatter:off
    public String loadDataFileView(@RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        // @formatter:on

        try {
            DataFile dataFile = dataFileService.findById(id);
            DataFileView view = new DataFileView(dataFile);
            model.addAttribute("view", view);

            return "dataFileView";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dataFileList";
        }
    }

    @PostMapping("/dataFileUpload")
    public String processUpload(@RequestParam(value = "multipartFile") MultipartFile multipartFile, Model model,
            RedirectAttributes redirectAttributes) {

        // Part 1: Upload a file, read it, and save it in a data file database entity.
        if (multipartFile.isEmpty()) {
            // No need to log this. I press UPLOAD without selecting a file all the time.
            redirectAttributes.addFlashAttribute("errorMessage", UPLOADED_FILE_NOT_FOUND_ERROR);
            return "redirect:/";
        }

        // Try to upload the file and save it to the database.
        DataFile dataFile = new DataFile();
        dataFile.setOriginalFilename(multipartFile.getOriginalFilename());
        dataFile.setContentType(multipartFile.getContentType());
        dataFile.setSize(multipartFile.getSize());
        Date uploadTimestamp = new Date();
        dataFile.setUploadTimestamp(uploadTimestamp);
        dataFile.setStatus(DataFileStatus.UPLOADED);

        // Read the lines of the file.
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
            String line;
            int seq = 0;
            while ((line = reader.readLine()) != null) {
                DataLine dataLine = new DataLine();
                dataLine.setDataFile(dataFile);
                dataLine.setSeq(seq++);
                dataLine.setText(line);
                dataFile.getDataLines().add(dataLine);
            }

            // Save the file.
            DataFile save = dataFileService.save(dataFile);
            String successMessage = String.format(SUCCESSFULLY_ADDED, "Data File", save.getId());
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("id", save.getId());
            return "redirect:/dataFile?id={id}";
        } catch (IOException e) {
            LOGGER.error(UPLOADED_FILE_NOT_READ_ERROR, e);
            redirectAttributes.addFlashAttribute("errorMessage", UPLOADED_FILE_NOT_READ_ERROR);
            return "redirect:/";
        } catch (OptimisticLockingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                    LOGGER.warn(UPLOADED_FILE_NOT_CLOSED_IGNORED, ignored);
                }
            }
        }
    }
}
