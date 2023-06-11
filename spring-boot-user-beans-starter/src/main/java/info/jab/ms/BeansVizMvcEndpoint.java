package info.jab.ms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class BeansVizMvcEndpoint  {

	@Autowired
	private BeansVizMvcHandler beansVizMvcHandler;

	@GetMapping(path= "/beansviz", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> beansviz() {
		return beansVizMvcHandler.beansviz();
	}

}