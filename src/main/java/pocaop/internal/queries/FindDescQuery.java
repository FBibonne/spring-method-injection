package pocaop.internal.queries;

import lombok.NonNull;

import java.time.LocalDate;
import java.util.Optional;

public record FindDescQuery(@NonNull String typeOrigine) implements QueryWrapper {
    

    public String interpolate(@NonNull String code, Optional<LocalDate> Optionaldate) {
        LocalDate date = Optionaldate.orElse(LocalDate.now());
        return STR."""
                PREFIX igeo: <http://rdf.insee.fr/def/geo#>
                PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
                PREFIX insee:<http://rdf.insee.fr/def/base#>
                SELECT DISTINCT ?uri ?code ?type ?typeArticle ?intitule ?intituleSansArticle ?dateCreation ?dateSuppression ?chefLieu ?typeDIris
                FROM <http://rdf.insee.fr/graphes/geo/cog>
                		WHERE {
                			?origine a igeo:\{typeOrigine} ;
                			       igeo:codeINSEE '\{code}' .
                			?uri igeo:subdivisionDirecteDe+ ?origine .
                			?uri igeo:codeINSEE ?code ;
                			     a ?type ;
                				 igeo:codeArticle ?typeArticle ;
                				 igeo:nom ?intitule ;
                				 igeo:nomSansArticle ?intituleSansArticle .
                    Optional{
                       ?uri	igeo:typeDIRIS ?uriTypeDIris ;
                        BIND(SUBSTR(STR(?uriTypeDIris ), STRLEN(STR(?uriTypeDIris )), 1) AS ?typeDIris)
                    }
                			OPTIONAL {
                			 	?uri (igeo:sousPrefecture|igeo:prefecture|igeo:prefectureDeRegion|igeo:bureauCentralisateur) ?chefLieuRDF .
                			 	?chefLieuRDF igeo:codeINSEE ?chefLieu .
                			 	OPTIONAL {
                					?evenementCreationChefLieu igeo:creation ?chefLieuRDF ;
                					igeo:date ?dateCreationChefLieu .
                				}
                				OPTIONAL {
                					?evenementSuppressionChefLieu igeo:suppression ?chefLieuRDF ;
                					igeo:date ?dateSuppressionChefLieu.
                				}
                					FILTER(!BOUND(?dateCreationChefLieu) || ?dateCreationChefLieu <= '\{date}'^^xsd:date)
                					FILTER(!BOUND(?dateSuppressionChefLieu) || ?dateSuppressionChefLieu > '\{date}'^^xsd:date)
                			}
                			OPTIONAL {
                				?evenementCreationOrigine igeo:creation ?origine ;
                					               igeo:date ?dateCreationOrigine.
                			}
                			OPTIONAL {
                				?evenementSuppressionOrigine igeo:suppression ?origine ;
                					                  igeo:date ?dateSuppressionOrigine.
                			}
                            OPTIONAL {
                			FILTER(!BOUND(?dateCreationOrigine) || ?dateCreationOrigine <= '\{date}'^^xsd:date)
                			}
                			OPTIONAL {
                			FILTER(!BOUND(?dateSuppressionOrigine) || ?dateSuppressionOrigine > '\{date}'^^xsd:date)
                            }
                			OPTIONAL {
                				?evenementCreation igeo:creation ?uri ;
                					               igeo:date ?dateCreation .
                			}
                			OPTIONAL {
                				?evenementSuppression igeo:suppression ?uri ;
                					                  igeo:date ?dateSuppression.
                			}

                			FILTER(!BOUND(?dateCreation) || ?dateCreation <= '\{date}'^^xsd:date)
                			FILTER(!BOUND(?dateSuppression) || ?dateSuppression > '\{date}'^^xsd:date)
                		}
                		ORDER BY ?type ?code
                """;
    }
}
