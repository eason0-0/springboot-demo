package online.springboot.demo.service.website;

public interface WebsiteUserService {

	/**
	 * ��¼��֤
	 * @param name
	 * @param password
	 * @return
	 */
	Boolean validating(String name, String password);

}
