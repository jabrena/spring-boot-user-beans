package info.jab.userbeans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.actuate.beans.BeansEndpoint.BeanDescriptor;
import org.springframework.boot.actuate.beans.BeansEndpoint.ContextBeansDescriptor;
import org.springframework.stereotype.Service;

import info.jab.userbeans.UserDependenciesService.DependencyBeanDetail;

@Service
public class Graph2Service {

    Logger logger = LoggerFactory.getLogger(Graph2Service.class);

	@Autowired
	private BeansEndpoint beansEndpoint;

	@Autowired
	private UserDependenciesService userDependenciesService;

	String generateHTML() {
		String html = "";
		try {
			html = Files.readString(Paths.get(getClass().getClassLoader().getResource("static/graph2.html").toURI()));
		} catch (IOException | URISyntaxException e) {
			logger.warn(e.getMessage(), e);
		}
		return html;
	}

	String generateGraph2(String param) {

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

		if(Objects.nonNull(param)) {
			logger.debug("Filtering by: {}", param);

			var results = userDependenciesService.getDependenciesAndBeans();
			var resultsFilterd = results.stream()
				.filter(dbd -> dbd.dependencyName().equals(param))
				.toList();

			var list2 = listDependencies.stream()
				.sorted(Comparator.comparing(Edge::from))
				.toList();

			var list3 = list2.stream()
				.filter(e -> resultsFilterd.stream()
					.map(DependencyBeanDetail::beanName)
					.toList().contains(e.from))
				.toList();

			return generateJSON(list3);
		}

		return generateJSON(listDependencies);
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

	public record DependencyCombo(String dependency, String value, Long counter) {}

	public List<DependencyCombo> generateGraph2Combo() {
		Map<String, Long> beanCountPerJar = userDependenciesService.getDependenciesAndBeans().stream()
			.collect(Collectors.groupingBy(DependencyBeanDetail::dependencyName, Collectors.counting()));

		return beanCountPerJar.entrySet().stream()
			.map(e -> new DependencyCombo(e.getKey(), e.getKey() + " (" + e.getValue() + ")", e.getValue()))
			.sorted(Comparator.comparing(DependencyCombo::dependency))
			.toList();
	}
}
