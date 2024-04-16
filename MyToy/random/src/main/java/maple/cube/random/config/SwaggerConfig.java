package maple.cube.random.config;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.core.jackson.ModelResolver;

@Configuration
public class SwaggerConfig {

	public ModelResolver modelResolver (ObjectMapper objectMapper) {
		return new ModelResolver(objectMapper);
	}

}
