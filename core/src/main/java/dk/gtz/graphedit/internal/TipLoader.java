package dk.gtz.graphedit.internal;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import dk.gtz.graphedit.viewmodel.Tip;
import dk.gtz.graphedit.viewmodel.TipContainer;

public class TipLoader {
	private static final Logger logger = LoggerFactory.getLogger(TipLoader.class);

	public static record Tips(ArrayList<Tip> tips) {
	}

	public static TipContainer loadTips() {
		try {
			var mapper = new ObjectMapper(new YAMLFactory());
			mapper.registerModule(new Jdk8Module());
			var tips = mapper.readValue(TipLoader.class.getClassLoader().getResourceAsStream("tips/tips.yml"), Tips.class);
			return new TipContainer(tips.tips());
		} catch (Exception e) {
			logger.error("Failed to load tips", e);
			return new TipContainer(List.of(new Tip("Error", "Failed to load tips")));
		}
	}
}
