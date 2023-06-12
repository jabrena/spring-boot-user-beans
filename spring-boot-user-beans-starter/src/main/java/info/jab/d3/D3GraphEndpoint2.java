package info.jab.d3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.actuate.beans.BeansEndpoint.BeanDescriptor;
import org.springframework.boot.actuate.beans.BeansEndpoint.ContextBeansDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class D3GraphEndpoint2  {

	@Autowired
	private BeansEndpoint beansEndpoint;

	@GetMapping(path= "/graph2", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> generateGraph2() {
		
		List<Edge> listDependencies = new ArrayList<>();

		Map<String, ContextBeansDescriptor> context = beansEndpoint.beans().getContexts();
		context.forEach((key, value) -> {

			Map<String, BeanDescriptor> beans = value.getBeans();
			beans.forEach((key2, value2) -> {
				String source = value2.getType().getSimpleName();

				List<String> dependencies = Arrays.asList(value2.getDependencies());
				dependencies.stream().forEach(dep -> {
					var depParts = dep.split("\\.");
					String dependencyValue = (depParts.length > 0) ? depParts[depParts.length - 1] : dep;
					listDependencies.add(new Edge(source, dependencyValue));
				});

			});
		});

		return ResponseEntity
				.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(generateJSON(listDependencies));
	}

	private record Edge(String from, String to) {};

	//TODO Not generate the response as a String.
	private String generateJSON(List<Edge> nodeLinkList) {

		StringBuilder result = new StringBuilder();

		result.append("[\n");

		for(Edge linkNode : nodeLinkList) {
			if(linkNode.equals(nodeLinkList.get(nodeLinkList.size() - 1))) {
				result.append("        {\"source\": \"" + linkNode.from() + "\", \"target\": \"" + linkNode.to() + "\", \"type\": \"licensing\"}\n");
			} else {
				result.append("        {\"source\": \"" + linkNode.from() + "\", \"target\": \"" + linkNode.to() + "\", \"value\": \"licensing\"},\n");
			}
		}

		result.append("]\n");

		return result.toString();
	}
}