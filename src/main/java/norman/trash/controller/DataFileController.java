package norman.trash.controller;

import norman.trash.controller.view.DataFileListForm;
import norman.trash.controller.view.DataFileView;
import norman.trash.controller.view.DataLineListForm;
import norman.trash.controller.view.DataTranListForm;
import norman.trash.domain.DataFile;
import norman.trash.domain.DataFileStatus;
import norman.trash.domain.DataLine;
import norman.trash.domain.DataTran;
import norman.trash.exception.NotFoundException;
import norman.trash.exception.OfxParseException;
import norman.trash.exception.OptimisticLockingException;
import norman.trash.service.DataFileService;
import norman.trash.service.DataLineService;
import norman.trash.service.DataTranService;
import norman.trash.service.OfxService;
import norman.trash.service.response.OfxAcct;
import norman.trash.service.response.OfxInst;
import norman.trash.service.response.OfxParseResponse;
import norman.trash.service.response.OfxStmtTran;
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
    private static final String[] dataLineSortableColumns = {"seq"};
    private static final String[] dataTranSortableColumns = {"type", "postDate", "amount", "checkNumber", "name"};
    @Autowired
    private DataFileService dataFileService;
    @Autowired
    private DataLineService dataLineService;
    @Autowired
    private DataTranService dataTranService;
    @Autowired
    private OfxService ofxService;

    @GetMapping("/dataFileList")
    // @formatter:off
    public String loadDataFileList(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
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

    @PostMapping("/dataUpload")
    public String processDataUpload(@RequestParam(value = "multipartFile") MultipartFile multipartFile, Model model,
            RedirectAttributes redirectAttributes) {
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
            return "redirect:/dataUploaded?id={id}";
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

    @GetMapping("/dataUploaded")
    // @formatter:off
    public String loadDataUploaded(@RequestParam("id") Long id,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "sortColumn", required = false, defaultValue = "seq") String sortColumn,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") Sort.Direction sortDirection,
            Model model,
            RedirectAttributes redirectAttributes) {
        // @formatter:on

        try {
            DataFile dataFile = dataFileService.findById(id);
            DataFileView view = new DataFileView(dataFile);
            model.addAttribute("view", view);

            // Convert sort column from string to an array of strings.
            String[] sortColumns = {defaultSortColumn};
            if (Arrays.asList(dataLineSortableColumns).contains(sortColumn)) {
                sortColumns = new String[]{sortColumn, defaultSortColumn};
            }

            // Get a page of records.
            PageRequest pageable = PageRequest.of(pageNumber, pageSize, sortDirection, sortColumns);
            Page<DataLine> page = dataLineService.findByDataFileId(id, pageable);
            DataLineListForm listForm = new DataLineListForm(page);
            model.addAttribute("listForm", listForm);
            return "dataUploaded";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dataFileList";
        }
    }

    @GetMapping("/dataParse")
    public String processDataParse(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            DataFile dataFile = dataFileService.findById(id);
            OfxParseResponse response = ofxService.parseDataFile(dataFile);

            OfxInst ofxInst = response.getOfxInst();
            dataFile.setOrganization(ofxInst.getOrganization());
            dataFile.setFid(ofxInst.getFid());

            OfxAcct ofxAcct = response.getOfxAcct();
            dataFile.setBankId(ofxAcct.getBankId());
            dataFile.setAcctId(ofxAcct.getAcctId());
            dataFile.setType(ofxAcct.getType());

            for (OfxStmtTran ofxStmtTran : response.getOfxStmtTrans()) {
                DataTran dataTran = new DataTran();
                dataTran.setType(ofxStmtTran.getType());
                dataTran.setPostDate(ofxStmtTran.getPostDate());
                dataTran.setUserDate(ofxStmtTran.getUserDate());
                dataTran.setAmount(ofxStmtTran.getAmount());
                dataTran.setFitId(ofxStmtTran.getFitId());
                dataTran.setSic(ofxStmtTran.getSic());
                dataTran.setCheckNumber(ofxStmtTran.getCheckNumber());
                dataTran.setCorrectFitId(ofxStmtTran.getCorrectFitId());
                dataTran.setCorrectAction(ofxStmtTran.getCorrectAction());
                dataTran.setName(ofxStmtTran.getName());
                dataTran.setCategory(ofxStmtTran.getCategory());
                dataTran.setMemo(ofxStmtTran.getMemo());
                dataTran.setDataFile(dataFile);
                dataFile.getDataTrans().add(dataTran);
            }

            dataFile.setStatus(DataFileStatus.PARSED);

            DataFile save = dataFileService.save(dataFile);
            String successMessage = String.format(SUCCESSFULLY_UPDATED, "Data File", save.getId());
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("id", save.getId());
            return "redirect:/dataParsed?id={id}";
        } catch (NotFoundException | OptimisticLockingException | OfxParseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dataFileList";
        }
    }

    @GetMapping("/dataParsed")
    // @formatter:off
    public String loadDataParsed(@RequestParam("id") Long id,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "sortColumn", required = false, defaultValue = "postDate") String sortColumn,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") Sort.Direction sortDirection,
            Model model,
            RedirectAttributes redirectAttributes) {
        // @formatter:on
        try {
            DataFile dataFile = dataFileService.findById(id);
            DataFileView view = new DataFileView(dataFile);
            model.addAttribute("view", view);

            // Convert sort column from string to an array of strings.
            String[] sortColumns = {defaultSortColumn};
            if (Arrays.asList(dataTranSortableColumns).contains(sortColumn)) {
                sortColumns = new String[]{sortColumn, defaultSortColumn};
            }

            // Get a page of records.
            PageRequest pageable = PageRequest.of(pageNumber, pageSize, sortDirection, sortColumns);
            Page<DataTran> page = dataTranService.findByDataFileId(id, pageable);
            DataTranListForm listForm = new DataTranListForm(page);
            model.addAttribute("listForm", listForm);
            return "dataParsed";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dataFileList";
        }
    }
}
