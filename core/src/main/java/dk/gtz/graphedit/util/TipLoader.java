package dk.gtz.graphedit.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import dk.gtz.graphedit.viewmodel.Tip;
import dk.gtz.graphedit.viewmodel.TipContainer;

/**
 * This class is responsible for loading tips from a YAML file.
 */
public class TipLoader {
    private static final Logger logger = LoggerFactory.getLogger(TipLoader.class);

    private static record Tips(ArrayList<Tip> tips) {}
    
    private TipLoader() {
    }

    /**
     * Load tips from the given file
     * @param tipsFile The file to load tips from
     * @return A container with the loaded tips
     */
    public static TipContainer loadTips(String tipsFile) {
        try {
            var mapper = new ObjectMapper(new YAMLFactory());
            mapper.registerModule(new Jdk8Module());
            var tips = mapper.readValue(TipLoader.class.getClassLoader().getResourceAsStream(tipsFile), Tips.class);
            return new TipContainer(tips.tips());
        } catch (Exception e) {
            logger.error("Failed to load tips", e);
            return new TipContainer(List.of(new Tip("Error", "Failed to load tips file [font=monospace]'%s'[/font]".formatted(tipsFile))));
        }
    }

    /**
     * Load tips from the default file
     * @return A container with the loaded tips
     */
    public static TipContainer loadTips() {
        return loadTips("tips/tips.yml");
    }
}
