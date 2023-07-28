package root.core.validation;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

//14.4
public class FileContentTypeValidator_ruim implements ConstraintValidator<FileContentType, MultipartFile> {

	private List<MediaType> mediaTypesPermitted;
	
	@Override
	public void initialize(FileContentType constraintAnnotation) {
		
		String[] allowed = constraintAnnotation.allowed();
		mediaTypesPermitted = new ArrayList<>();
		
		for (int i = 0; i < allowed.length; i++) {
			mediaTypesPermitted.add(MediaType.valueOf(allowed[i]));
		} 
	}
	
	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
		
		if (mediaTypesPermitted.contains(MediaType.valueOf(value.getContentType() )))
			return true;
		return false;
	}

}