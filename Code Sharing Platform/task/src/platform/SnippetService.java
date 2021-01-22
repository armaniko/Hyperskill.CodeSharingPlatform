package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SnippetService {

    @Autowired
    private SnippetRepository repository;

    private static final String DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    public String addCodeSnippet(String code, int time, int views) {
        CodeSnippet snippet = repository.save(new CodeSnippet(code, getCurrentDateTime(), time, views));
        return snippet.getUuid();
    }

    public CodeSnippet getCodeSnippet(String id) {
        CodeSnippet codeSnippet = repository.findByUuid(id).orElseThrow();
        CodeSnippet codeSnippetForReturn = null;
        try {
            codeSnippetForReturn = (CodeSnippet) codeSnippet.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (codeSnippetForReturn.checkTime()) {
            repository.delete(codeSnippet);
            throw new NoSuchElementException();
        } else if (codeSnippetForReturn.viewCodeSnippet()){
            repository.delete(codeSnippet);
        } else {
            repository.save(codeSnippetForReturn);
        }
        return codeSnippetForReturn;
    }

    public List<CodeSnippet> getLatestCodeSnippets(){
        return repository.findAllLatest();
    }

    private String getCurrentDateTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        return localDateTime.format(dateTimeFormatter);
    }

}
