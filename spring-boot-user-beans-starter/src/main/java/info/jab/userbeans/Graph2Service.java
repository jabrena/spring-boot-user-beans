package info.jab.userbeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.actuate.beans.BeansEndpoint.BeanDescriptor;
import org.springframework.boot.actuate.beans.BeansEndpoint.ContextBeansDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class Graph2Service {

	@Autowired
	private BeansEndpoint beansEndpoint;

	@Autowired
	private UserDependenciesService userDependenciesService;

	ResponseEntity<String> generateGraph2(String param) {

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

		/*
		if(!Objects.isNull(param)) {
			var filterList = userDependenciesService.getDependenciesAndBeans().stream()
				.filter(dd -> dd.dependencyName().equals(param))
				//.peek(System.out::println)
				.toList();

			System.out.println(source);
			if (filterList.contains(source)) {
				listDependencies.add(new Edge(source, dependencyValue));
			}
		} else {
			listDependencies.add(new Edge(source, dependencyValue));
		}
		*/


		System.out.println("----");

		if(!Objects.isNull(param)) {
			var filterList = userDependenciesService.getDependenciesAndBeans().stream()
				.filter(dd -> dd.dependencyName().equals(param))
				.map(dd -> dd.beanName())
				.peek(System.out::println)
				.toList();
		}


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
