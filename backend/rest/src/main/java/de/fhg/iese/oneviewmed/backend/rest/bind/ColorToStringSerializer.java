package de.fhg.iese.oneviewmed.backend.rest.bind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.awt.Color;
import java.io.IOException;
import java.util.Locale;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class ColorToStringSerializer extends JsonSerializer<Color> {

  @Override
  public void serialize(final Color value, final JsonGenerator jsonGenerator,
                        final SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeString(toHtml(value));
  }

  private static String toHtml(final Color color) {
    return String.format(Locale.ROOT, "#%02x%02x%02x",
        color.getRed(), color.getGreen(), color.getBlue());
  }

}
