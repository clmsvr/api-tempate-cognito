package root.core.validation;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

//14.4
public class FileContentTypeValidator implements ConstraintValidator<FileContentType, MultipartFile> {

	private List<String> mediaTypesPermitted;
	
	@Override
	public void initialize(FileContentType constraintAnnotation) {
		mediaTypesPermitted = Arrays.asList(constraintAnnotation.allowed());
	}
	
	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
		
		return value == null 
                || this.mediaTypesPermitted.contains(value.getContentType());
	}

}