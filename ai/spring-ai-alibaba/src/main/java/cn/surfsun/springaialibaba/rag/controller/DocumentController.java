package cn.surfsun.springaialibaba.rag.controller;


import cn.surfsun.springaialibaba.rag.entity.Document;
import cn.surfsun.springaialibaba.rag.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 文档管理控制器
 */
@Slf4j
@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * 文档上传页面
     */
    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    /**
     * 处理文档上传
     */
    @PostMapping("/upload")
    public String uploadDocument(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "请选择要上传的文件");
                return "redirect:/documents/upload";
            }

            // 检查文件类型
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".txt") && !fileName.endsWith(".md"))) {
                redirectAttributes.addFlashAttribute("error", "只支持 .txt 和 .md 格式的文件");
                return "redirect:/documents/upload";
            }

            Document document = documentService.uploadDocument(file);
            redirectAttributes.addFlashAttribute("success",
                    "文档上传成功！文件名：" + document.getFileName());

            return "redirect:/documents";
        } catch (Exception e) {
            log.error("文档上传失败", e);
            redirectAttributes.addFlashAttribute("error", "上传失败：" + e.getMessage());
            return "redirect:/documents/upload";
        }
    }

    /**
     * 文档列表页面
     */
    @GetMapping
    public String listDocuments(Model model) {
        List<Document> documents = documentService.getAllDocuments();
        model.addAttribute("documents", documents);
        return "documents";
    }

    /**
     * 删除文档
     */
    @PostMapping("/delete/{id}")
    public String deleteDocument(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            documentService.deleteDocument(id);
            redirectAttributes.addFlashAttribute("success", "文档删除成功");
        } catch (Exception e) {
            log.error("文档删除失败", e);
            redirectAttributes.addFlashAttribute("error", "删除失败：" + e.getMessage());
        }
        return "redirect:/documents";
    }
}
