package norman.trash.controller;

import norman.trash.TrashUtils;
import norman.trash.controller.view.*;
import norman.trash.domain.*;
import norman.trash.exception.NotFoundException;
import norman.trash.exception.OfxParseException;
import norman.trash.exception.OptimisticLockingException;
import norman.trash.service.*;
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
import java.util.*;

import static norman.trash.MessagesConstants.*;

@Controller
public class DataFileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataFileController.class);
    private static final String defaultSortColumn = "id";
    private static final String[] dataFileSortableColumns =
            {"originalFilename", "contentType", "uploadTimestamp", "status"};
    private static final String[] dataLineSortableColumns = {"seq"};
    @Autowired
    private DataFileService dataFileService;
    @Autowired
    private DataLineService dataLineService;
    @Autowired
    private OfxService ofxService;
    @Autowired
    private AcctService acctService;
    @Autowired
    private AcctNbrService acctNbrService;
    @Autowired
    private StmtService stmtService;

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
            return "redirect:/dataParse?id={id}";
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

    @GetMapping("/dataParse")
    // @formatter:off
    public String loadDataParse(@RequestParam("id") Long id,
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
            return "dataParse";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dataFileList";
        }
    }

    @PostMapping("/dataParse")
    public String processDataParse(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            DataFile dataFile = dataFileService.findById(id);
            OfxParseResponse response = ofxService.parseDataFile(dataFile);

            OfxInst ofxInst = response.getOfxInst();
            dataFile.setOfxOrganization(ofxInst.getOrganization());
            dataFile.setOfxFid(ofxInst.getFid());

            OfxAcct ofxAcct = response.getOfxAcct();
            dataFile.setOfxBankId(ofxAcct.getBankId());
            dataFile.setOfxAcctId(ofxAcct.getAcctId());
            dataFile.setOfxType(ofxAcct.getType());

            for (OfxStmtTran ofxStmtTran : response.getOfxStmtTrans()) {
                DataTran dataTran = new DataTran();
                dataTran.setOfxType(ofxStmtTran.getType());
                dataTran.setOfxPostDate(ofxStmtTran.getPostDate());
                dataTran.setOfxUserDate(ofxStmtTran.getUserDate());
                dataTran.setOfxAmount(ofxStmtTran.getAmount());
                dataTran.setOfxFitId(ofxStmtTran.getFitId());
                dataTran.setOfxSic(ofxStmtTran.getSic());
                dataTran.setOfxCheckNumber(ofxStmtTran.getCheckNumber());
                dataTran.setOfxCorrectFitId(ofxStmtTran.getCorrectFitId());
                dataTran.setOfxCorrectAction(ofxStmtTran.getCorrectAction());
                dataTran.setOfxName(ofxStmtTran.getName());
                dataTran.setOfxCategory(ofxStmtTran.getCategory());
                dataTran.setOfxMemo(ofxStmtTran.getMemo());
                dataTran.setDataFile(dataFile);
                dataFile.getDataTrans().add(dataTran);
            }
            dataFile.setStatus(DataFileStatus.PARSED);

            DataFile save = dataFileService.save(dataFile);
            String successMessage = String.format(SUCCESSFULLY_UPDATED, "Data File", save.getId());
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("id", save.getId());
            return "redirect:/dataAcctMatch?id={id}";
        } catch (NotFoundException | OptimisticLockingException | OfxParseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dataFileList";
        }
    }

    @GetMapping("/dataAcctMatch")
    public String loadDataAcctMatch(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // If we've already matched an account to this data file, we need to match transactions.
            DataFile dataFile = dataFileService.findById(id);
            if (dataFile.getAcct() != null) {
                redirectAttributes.addAttribute("id", id);
                redirectAttributes.addAttribute("acctId", dataFile.getAcct().getId());
                return "redirect:/dataTranMatch?id={id}&acctId={acctId}";
            }

            // Does this account already exist? Do multiple accounts exist? Try to find out using financial institution
            // id (which identifies the bank) and the account id (which is the account number).
            String ofxFid = dataFile.getOfxFid();
            String ofxAcctId = dataFile.getOfxAcctId();
            List<AcctNbr> acctNbrs = acctNbrService.findByAcctOfxFidAndNumber(ofxFid, ofxAcctId);
            Map<Long, Acct> acctMap = new HashMap<>();
            for (AcctNbr acctNbr : acctNbrs) {
                Acct acct = acctNbr.getAcct();
                acctMap.put(acct.getId(), acct);
            }

            // If we found exactly one account, we have found the matching account. Now we need to match transactions.
            if (acctMap.size() == 1) {
                Acct acct = acctMap.values().iterator().next();
                redirectAttributes.addAttribute("id", id);
                redirectAttributes.addAttribute("acctId", acct.getId());
                return "redirect:/dataTranMatch?id={id}&acctId={acctId}";
            }

            // Otherwise, we found no accounts (or possibly many accounts). Now we  need to go to an account
            // disambiguation page to get input from the user.
            DataFileView view = new DataFileView(dataFile);
            model.addAttribute("view", view);

            // Get accounts that have the same financial institution id.
            List<Acct> sameFidAccts = acctService.findByOfxFid(ofxFid);

            // Get accounts that have no financial institution id.
            List<Acct> noFidAccts = acctService.findByOfxFidNull();

            DataAcctMatchView dataAcctMatchView = new DataAcctMatchView(sameFidAccts, noFidAccts);
            model.addAttribute("dataAcctMatchView", dataAcctMatchView);

            return "dataAcctMatch";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dataFileList";
        }
    }

    @GetMapping("/dataTranMatch")
    // @formatter:off
    public String loadDataTranMatch(@RequestParam("id") Long id,
            @RequestParam("acctId") Long acctId,
            Model model,
            RedirectAttributes redirectAttributes) {
        // @formatter:on
        try {
            DataFile dataFile = dataFileService.findById(id);
            DataFileView view = new DataFileView(dataFile);
            model.addAttribute("view", view);

            Date endOfTime = TrashUtils.getEndOfTime();
            Stmt stmt = stmtService.findByAcctIdAndCloseDate(acctId, endOfTime);
            List<String> alreadyMatchedFitIds = new ArrayList<>();
            List<Tran> unmatchedTrans = new ArrayList<>();
            for (Tran tran : stmt.getTrans()) {
                String ofxFitId = tran.getOfxFitId();
                if (ofxFitId != null) {
                    alreadyMatchedFitIds.add(ofxFitId);
                } else {
                    unmatchedTrans.add(tran);
                }
            }

            List<DataTranMatchRow> rows = new ArrayList<>();
            for (DataTran dataTran : dataFile.getDataTrans()) {
                if (!alreadyMatchedFitIds.contains(dataTran.getOfxFitId())) {
                    DataTranMatchRow row = new DataTranMatchRow(dataTran, unmatchedTrans);
                    rows.add(row);
                }
            }
            DataTranMatchForm dataTranMatchForm = new DataTranMatchForm(rows);
            model.addAttribute("dataTranMatchForm", dataTranMatchForm);

            return "dataTranMatch";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dataFileList";
        }
    }
}
