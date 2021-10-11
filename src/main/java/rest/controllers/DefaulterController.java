package rest.controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import rest.dao.DefaultersDao;

@CrossOrigin(originPatterns = "*")
@RestController
public class DefaulterController {

	DefaultersDao defaulterDao = new DefaultersDao();

	@RequestMapping(value = "/def", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getList(HttpServletRequest request, @RequestParam(defaultValue = "all") String flatNumber) {
		String result = defaulterDao.getList(flatNumber);
		return result;
	}

}