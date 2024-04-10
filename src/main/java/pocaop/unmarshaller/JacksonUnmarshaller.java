package pocaop.unmarshaller;

import bibonne.exp.oascache.metadata.api.model.Commune;
import bibonne.exp.oascache.metadata.api.model.Departement;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;
import pocaop.internal.Unmarshaller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

@Component
@Slf4j
@RegisterReflectionForBinding(classes = {Commune.class, Departement.class})
public record JacksonUnmarshaller(CsvMapper csvMapper) implements Unmarshaller {

    public JacksonUnmarshaller() {
        this(CsvMapper.csvBuilder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .addModule(articleEnumModule())
                .addModule(new JavaTimeModule())
                .addModule(new JsonNullableModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build());
    }

    private static Module articleEnumModule() {
        var module = new SimpleModule();
        module.addDeserializer(Commune.TypeArticleEnum.class, new JsonDeserializer<>() {
            @Override
            public Commune.TypeArticleEnum deserialize(JsonParser parser, DeserializationContext ctxt) {
                try {
                    return Commune.TypeArticleEnum.values()[Integer.parseInt(parser.getValueAsString())];
                } catch (NumberFormatException | IOException e) {
                    return Commune.TypeArticleEnum._0_ARTICLE_NULL_CHARNIERE_DE_;
                }
            }
        });
        return module;
    }

    @Override
    public Object unmarshal(@NonNull String csv, @NonNull Method calledMethod) {
        ReturnedType returnedType = new ReturnedType(calledMethod);
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        ObjectReader reader = csvMapper.readerFor(returnedType.typeForMapping()).with(schema);
        List<?> results;
        try (MappingIterator<?> mappingIterator = reader.readValues(csv)) {
            results = mappingIterator.readAll();
        } catch (IOException e) {
            log.error(STR."""
            While reading
            \{csv}
            MESSAGE : \{e.getMessage()}
            ===> RETURN WILL BE EMPTY
            """);
            return returnedType.isCollection() ? List.of() : null;
        }
        return returnedType.isCollection() ? results : results.getFirst();
    }
}
