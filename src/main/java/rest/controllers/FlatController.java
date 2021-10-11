package rest.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import rest.dao.FlatsDao;

@CrossOrigin(originPatterns = "*")
@RestController
public class FlatController {

	FlatsDao flatsDao = new FlatsDao();

	@RequestMapping(value = "/flats", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String loginUser() {
		String result = flatsDao.getFlats();
		return result;
	}
}