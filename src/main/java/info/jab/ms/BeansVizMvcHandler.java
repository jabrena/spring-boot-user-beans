package info.jab.ms;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.actuate.beans.BeansEndpoint.BeanDescriptor;
import org.springframework.boot.actuate.beans.BeansEndpoint.ContextBeansDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BeansVizMvcHandler {

	@Autowired
	private BeansEndpoint beansEndpoint;

	//@SuppressWarnings("unchecked")
	ResponseEntity<String> beansviz() {

		StringBuilder result = new StringBuilder();

		Map<String, ContextBeansDescriptor> context = beansEndpoint.beans().getContexts();
		context.forEach((key, value) -> {

			result.append("{\n");
			result.append("    \"nodes\": [\n");
			result.append("        {\"id\": \"" + value.toString() + "\", \"group\": \"" + value + "\"},\n");

			List<String> nodes = new ArrayList<>();
			Map<String, BeanDescriptor> beans = value.getBeans();
			beans.forEach((key2, value2) -> {
				result.append("        {\"id\": \"" + value2.getType().getSimpleName() + "\", \"group\": \"" + value2.getType().getSimpleName() + "\"},\n");
				nodes.add(value2.getType().getSimpleName());
			});

			result.append("        {\"id\": \"" + "LAST_NODE" + "\", \"group\": \"" +  "LAST_NODE" + "\"}\n");
			result.append("    ],\n");
			result.append("    \"links\": [\n");

			Map<String, BeanDescriptor> beans2 = value.getBeans();
			beans2.forEach((key2, value2) -> {

				String source = value2.getType().getSimpleName();

				List<String> dependencies = Arrays.asList(value2.getDependencies());
				dependencies.stream().forEach(dep -> {
					var depParts = dep.split("\\.");
					String dependencyValue = (depParts.length > 0) ? depParts[depParts.length - 1] : dep;
					if(nodes.contains(dependencyValue)) {
						result.append("        {\"source\": \"" + source + "\", \"target\": \"" + dependencyValue + "\", \"value\": 1},\n");
					}
				});
			});

			result.append("        {\"source\": \"" + "LAST_NODE" + "\", \"target\": \"" + "LAST_NODE" + "\", \"value\": 1}\n");
			result.append("    ]\n");
			result.append("}\n");

		});

		return ResponseEntity
				.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(result.toString());
	}
}