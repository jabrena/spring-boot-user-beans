package info.jab.userbeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import info.jab.userbeans.UserBeansService.BeanDetail;

import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserBeansController {

	@Autowired
	private UserBeansService userBeansService;

	@GetMapping(path= "/api/v1/user-beans/beans")
	ResponseEntity<List<BeanDetail>> getDependencies() {
		return ResponseEntity.ok(userBeansService.getBeansDetails());
	}
}
