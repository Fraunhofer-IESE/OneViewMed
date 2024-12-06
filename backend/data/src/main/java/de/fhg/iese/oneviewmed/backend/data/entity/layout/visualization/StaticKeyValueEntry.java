package de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization;


import lombok.Data;

@Data
public class StaticKeyValueEntry implements KeyValueEntry {
  private final String key;
  private final String keyName;
}
