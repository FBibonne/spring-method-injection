package pocaop.stubs;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import pocaop.internal.QueryExecutor;

@Component
public record StubQueryExecutor() implements QueryExecutor {

    private static final String CSV_2_ROWS = """
            "uri","code","type","typeArticle","intitule","intituleSansArticle","dateCreation","dateSuppression","chefLieu","categorieJuridique","intituleComplet"
            "http://id.insee.fr/geo/commune/72d08f78-9b5c-46c6-bdaf-b31ed46216a0","33529","Commune","3","La Teste","Teste","1943-01-01","1994-06-13",,,
            "http://id.insee.fr/geo/commune/34e3a788-bc74-4334-a764-209b5b45b3fc","33529","Commune","3","La Teste-de-Buch","Teste-de-Buch","1994-06-13",,,,
            """;
    private static final String CSV_EMPTY = """
            "uri","code","type","typeArticle","intitule","intituleSansArticle","dateCreation","dateSuppression","chefLieu","categorieJuridique","intituleComplet"
            """;

    @Override
    public String execute(@NonNull String query) {
       if (!(query.contains("igeo:Commune"))){
           throw new RuntimeException("Emulated database error");
       }
       if(!query.contains("33529")){
           return CSV_EMPTY;
       }
        return CSV_2_ROWS;
    }
}
