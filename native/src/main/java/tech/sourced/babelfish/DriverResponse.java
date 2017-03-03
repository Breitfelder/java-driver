package tech.sourced.babelfish;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Class for the java driver response
 */
public class DriverResponse {
    final public String driver;
    final public String language;
    final public String languageVersion;
    public String status = "ok";
    public ArrayList<String> errors = new ArrayList<String>(0);
    @JsonProperty("AST")
    public CompilationUnit cu;
    private ObjectMapper mapper;
    private JsonGenerator jG;

    /**
     * Create a new DriverResponse
     *
     * @param driver          version of the driver
     * @param language        language for which the driver is made, always java
     * @param languageVersion version of the language
     */
    public DriverResponse(String driver, String language, String languageVersion) {
        this.driver = driver;
        this.language = language;
        this.languageVersion = languageVersion;
    }

    @JsonIgnore
    /**
     * Set a previously configured Jackson ObjectMapper to driverResponse.
     *
     * @param responseMapper the mapper to set
     */
    public void setMapper(RequestResponseMapper.ResponseMapper responseMapper) {
        mapper = responseMapper.mapper;
        jG = responseMapper.jG;
    }

    /**
     * Parse the code inside source
     *
     * @param parser Parser used in the parsing
     * @param source Source code to parse
     */
    public void makeResponse(EclipseParser parser, String source) {
        try {
            cu = parser.parse(source);
        } catch (IOException e) {
            errors.add("IOException");
            errors.add(e.getMessage());
            status = "error";
        }
    }

    /**
     * Serialize DriverResponse in the output given by the requestMapper assigned before.
     *
     * @throws IOException when the write failed or mapper is not assigned
     */
    public void pack() throws IOException {
        if (mapper != null) {
            mapper.writeValue(jG, this);
        } else {
            throw new IOException("Mapper not assigned, use setMapper before packing");
        }
    }

    public boolean equals(DriverResponse o) {
        return this.status.equals(o.status) && this.cu == o.cu && this.driver.equals(driver) && this.language.equals(o.language) && this.languageVersion.equals(o.languageVersion);
    }
}

