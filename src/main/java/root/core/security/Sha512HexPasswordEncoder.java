package root.core.security;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

//minha implementacao de teste

public class Sha512HexPasswordEncoder implements PasswordEncoder{

	@Override
	public String encode(CharSequence rawPassword) {
		
		return  DigestUtils.sha512Hex(rawPassword.toString());
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) 
	{
		String raw = DigestUtils.sha512Hex(rawPassword.toString());
		return encodedPassword.equals(raw);
	}
}