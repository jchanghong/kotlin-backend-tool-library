package org.apache.myfaces.lombok;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LombokJava {
	String name;
}
