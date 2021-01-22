package platform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import freemarker.template.*;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RestController
public class Controller {

    @Autowired
    private SnippetService snippetService;

    //private List<CodeSnippet> snippetsList = new LinkedList<CodeSnippet>();
    private Map<String, Object> root;
    private static Configuration cfg;

    Controller () {
        try {
            initiateConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        root = new HashMap<String, Object>();
        root.put("title", "");
        root.put("codeSnippet", "");
    }

    @GetMapping(value = "/code/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String getCodeId(@PathVariable String id) {
        Writer out = new StringWriter();
        try {
            root.put("title", "Code");
            CodeSnippet codeSnippet = snippetService.getCodeSnippet(id);
            root.put("date", codeSnippet.getDate());
            root.put("isRestrictionPresent", codeSnippet.isRestrictionPresent());
            root.put("views", codeSnippet.getViews());
            root.put("time", codeSnippet.getTime());
            root.put("code", codeSnippet.getCode());
            Template temp = cfg.getTemplate("getCode.html");
            out = new StringWriter();
            temp.process(root, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            throw new CodeSnippetNotFoundException();
        } catch (NoSuchElementException e) {
            throw new CodeSnippetNotFoundException();
        } catch (MalformedTemplateNameException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (freemarker.core.ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    @GetMapping(value = "/code/latest", produces = MediaType.TEXT_HTML_VALUE)
    public String getCodeLatest() {
        Writer out = null;
        try {
            root.put("title", "Latest");
            root.put("codeSnippet", snippetService.getLatestCodeSnippets());
            Template temp = cfg.getTemplate("getCodeLatest.html");
            out = new StringWriter();
            temp.process(root, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            return out.toString();
        }
    }

    @GetMapping(value = "/code/new", produces = MediaType.TEXT_HTML_VALUE)
    public String getCodeNew() {
        Writer out = null;
        try {
            root.put("title", "Create");
            Template temp = cfg.getTemplate("getCodeNew.html");
            out = new StringWriter();
            temp.process(root, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            return out.toString();
        }
    }

    @GetMapping(value = "/api/code/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getApiCodeId(@PathVariable String id) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode snippet = mapper.createObjectNode();
        CodeSnippet codeSnippet = null;
        try {
            codeSnippet = snippetService.getCodeSnippet(id);
            snippet.put("code", codeSnippet.getCode());
            snippet.put("date", codeSnippet.getDate());
            snippet.put("time", codeSnippet.getTime());
            snippet.put("views", codeSnippet.getViews());
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(snippet);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (NoSuchElementException e) {
            throw new CodeSnippetNotFoundException();
        }
        return "{ }";
    }

    @GetMapping(value = "/api/code/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getApiCodeLatest() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode root = mapper.createArrayNode();
        List<CodeSnippet> output = snippetService.getLatestCodeSnippets();
        for (CodeSnippet snip : output) {
            ObjectNode element = mapper.createObjectNode();
            element.put("date", snip.getDate());
            element.put("code", snip.getCode());
            element.put("time", snip.getTime());
            element.put("views", snip.getViews());
            root.add(element);
        }
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "{ }";
    }


    @PostMapping(value = "/api/code/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public String postApiCodeNew(@RequestBody String json) {
        JSONParser parser = new JSONParser(json);
        String id = "";
        try {
            LinkedHashMap<String, Object> data = parser.object();
            id = snippetService.addCodeSnippet(data.get("code").toString(), Integer.parseInt(data.get("time").toString()), Integer.parseInt(data.get("views").toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "{ \"id\" : \"" + id + "\" }";
    }

    private void initiateConfig () throws IOException {
        cfg = new Configuration(Configuration.VERSION_2_3_29);
        cfg.setDirectoryForTemplateLoading(new File("C:\\Users\\armagedon\\IdeaProjects\\Code Sharing Platform\\Code Sharing Platform\\task\\src\\platform\\"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
    }
}
