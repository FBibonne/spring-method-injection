package pocaop.internal.queries;

import java.time.LocalDate;
import java.util.Optional;

public sealed abstract class SimpleQuery implements QueryWrapper permits FindAllQuery, FindByCodeQuery {

    protected String interpolate(Optional<String> code, Optional<LocalDate> date, String territoire){
        return STR."""
                PREFIX igeo: <http://rdf.insee.fr/def/geo#>
                PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
                PREFIX insee:<http://rdf.insee.fr/def/base#>
                SELECT DISTINCT ?uri ?code ?typeArticle ?intitule ?intituleSansArticle ?dateCreation ?dateSuppression ?chefLieu ?intitule ?categorieJuridique ?intituleComplet
                FROM <http://rdf.insee.fr/graphes/geo/cog>
                FROM <http://rdf.insee.fr/graphes/codes/cj>
                WHERE {
                			 	 ?uri a igeo:\{territoire} ;
                				 igeo:codeArticle ?typeArticle ;
                				 igeo:nom ?intitule ;
                				 igeo:nomSansArticle ?intituleSansArticle .
                				 OPTIONAL {
                					?uri a igeo:\{territoire} ;
                					insee:categorieJuridique ?cj.
                					?cj skos:prefLabel ?categorieJuridique.
                					}
                			\{code.map(c -> STR."""
                                          ?uri igeo:codeINSEE '\{c}' .
                                          BIND('\{c}' as ?code)
                                          """)
                .orElse("?uri igeo:codeINSEE ?code .")
                }
                			OPTIONAL {
                				?evenementCreation igeo:creation ?uri ;
                					               igeo:date ?dateCreation .
                			}
                			OPTIONAL {
                				?evenementSuppression igeo:suppression ?uri ;
                					                  igeo:date ?dateSuppression.
                			}
                			\{date.map(d -> STR."""
                                              			FILTER(!BOUND(?dateCreation) || ?dateCreation <= '\{d}'^^xsd:date)
                                              			FILTER(!BOUND(?dateSuppression) || ?dateSuppression > '\{d}'^^xsd:date)
                                              """).orElse("")}
                		}
                		ORDER BY ?code
                """;
    }

}
