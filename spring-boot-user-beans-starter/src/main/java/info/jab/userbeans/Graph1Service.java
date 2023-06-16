package info.jab.userbeans;

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
import org.springframework.stereotype.Service;

@Service
public class Graph1Service {

	@Autowired
	private BeansEndpoint beansEndpoint;

	ResponseEntity<String> generateGraph1Data() {

		List<String> listBean = new ArrayList<>();
		List<Edge> listDependencies = new ArrayList<>();

		Map<String, ContextBeansDescriptor> context = beansEndpoint.beans().getContexts();
		context.forEach((key, value) -> {

			Map<String, BeanDescriptor> beans = value.getBeans();
			beans.forEach((key2, value2) -> {
				String source = value2.getType().getSimpleName();
				listBean.add(source);

				List<String> dependencies = Arrays.asList(value2.getDependencies());
				dependencies.stream().forEach(dep -> {
					var depParts = dep.split("\\.");
					String dependencyValue = (depParts.length > 0) ? depParts[depParts.length - 1] : dep;
					if(listBean.contains(dependencyValue)) {
						listDependencies.add(new Edge(source, dependencyValue));
					}
				});

			});
		});

		return ResponseEntity
				.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(generateJSON(listBean, listDependencies));
	}

	private record Edge(String from, String to) {};

	//TODO Not generate the response as a String.
	private String generateJSON(List<String> beanList, List<Edge> nodeLinkList) {

		StringBuilder result = new StringBuilder();

		result.append("{\n");
		result.append("    \"nodes\": [\n");

		for(String beanName : beanList) {
			if(beanName.equals(beanList.get(beanList.size() - 1))) {
				result.append("        {\"id\": \"" + beanName + "\", \"group\": \"" + beanName + "\"}\n");
			} else {
				result.append("        {\"id\": \"" + beanName + "\", \"group\": \"" + beanName + "\"},\n");
			}
		}

		result.append("    ],\n");
		result.append("    \"links\": [\n");

		for(Edge linkNode : nodeLinkList) {
			if(linkNode.equals(nodeLinkList.get(nodeLinkList.size() - 1))) {
				result.append("        {\"source\": \"" + linkNode.from() + "\", \"target\": \"" + linkNode.to() + "\", \"value\": 1}\n");
			} else {
				result.append("        {\"source\": \"" + linkNode.from() + "\", \"target\": \"" + linkNode.to() + "\", \"value\": 1},\n");
			}
		}

		result.append("    ]\n");
		result.append("}\n");

		return result.toString();
	}

}
