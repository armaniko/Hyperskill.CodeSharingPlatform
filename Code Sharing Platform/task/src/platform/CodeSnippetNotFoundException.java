package platform;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Code Snippet not found")
public class CodeSnippetNotFoundException extends RuntimeException{
}
