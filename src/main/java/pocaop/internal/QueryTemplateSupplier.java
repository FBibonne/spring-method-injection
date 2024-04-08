package pocaop.internal;

import lombok.NonNull;
import pocaop.internal.providers.CodeProvider;
import pocaop.internal.providers.DateProvider;
import pocaop.internal.queries.FindAllQuery;
import pocaop.internal.queries.FindByCodeQuery;
import pocaop.internal.queries.FindDescQuery;
import pocaop.internal.queries.QueryWrapper;

import java.util.Optional;

public class QueryTemplateSupplier {

    public Optional<QueryTemplate> get(@NonNull String methodName) {
        return Optional.ofNullable(switch (methodName) {
            case "getcogcom" -> queryTemplate().withFindByCode()
                    .codeAtPosition(0)
                    .dateAtPosition(1)
                    .forTerritory("Commune");
            case "getcogdep" -> queryTemplate().withFindByCode()
                    .codeAtPosition(0)
                    .dateAtPosition(1)
                    .forTerritory("Departement");
            case "getTerritoireByTypeAndCode" -> queryTemplate().withFindByCode()
                    .codeAtPosition(0)
                    .dateAtPosition(2)
                    .forUnknownTerritoryType(1);
            case "getcogcomliste" -> queryTemplate().withFindAll()
                    .dateAtPosition(0)
                    .forTerritory("Commune");
            case "getcogdepdesc" -> queryTemplate().withFindDesc()
                    .codeAtPosition(0)
                    .dateAtPosition(1)
                    .forTerritory("Departement");
            default -> null;
        });
    }

    private QueryTemplateBuilder queryTemplate() {
        return new QueryTemplateBuilder();
    }

    private sealed interface QueryWrapperProvider{
    }

    private record QueryTemplateBuilder() {
        public QueryTemplateBuilderWithRequest withFindByCode() {
            return new QueryTemplateBuilderWithRequest(new FindByCodeProvider(), null, null);
        }

        public QueryTemplateBuilderWithRequest withFindAll() {
            return new QueryTemplateBuilderWithRequest(new AllQueryProvider(), null, null);
        }

        public QueryTemplateBuilderWithRequest withFindDesc() {
            return new QueryTemplateBuilderWithRequest(new FindDescProvider(), null, null);
        }

        private record FindByCodeProvider() implements QueryWrapperProvider {
        }

        private record AllQueryProvider() implements QueryWrapperProvider {
        }

        private record FindDescProvider() implements QueryWrapperProvider {
        }
    }

    private record QueryTemplateBuilderWithRequest(QueryWrapperProvider queryWrapperProvider,
                                                   CodeProvider codeProvider,
                                                   DateProvider dateProvider) {
        public QueryTemplate forTerritory(String territoire) {
            return new QueryTemplate(provide(this.queryWrapperProvider, territoire), _ -> territoire, this.codeProvider, this.dateProvider);
        }

        private QueryWrapper provide(@NonNull QueryWrapperProvider queryWrapperProvider, String territoire) {
            return switch (queryWrapperProvider){
                case QueryTemplateBuilder.AllQueryProvider _ -> FindAllQuery.INSTANCE;
                case QueryTemplateBuilder.FindByCodeProvider _ -> FindByCodeQuery.INSTANCE;
                case QueryTemplateBuilder.FindDescProvider _ -> {
                    if (territoire==null){
                        throw new UnsupportedOperationException("Impossible to provide FindDescQuery when territoire is null (ie it is impossible to find all desc territories of a unknown type of territory");
                    }
                    yield new FindDescQuery(territoire);
                }
            };
        }

        public QueryTemplate forUnknownTerritoryType(int position) {
            return new QueryTemplate(provide(this.queryWrapperProvider, null), args -> QueryTemplate.resolveAsString(args, position), this.codeProvider, this.dateProvider);
        }

        public QueryTemplateBuilderWithRequest codeAtPosition(int i) {
            return new QueryTemplateBuilderWithRequest(this.queryWrapperProvider, args -> QueryTemplate.resolveAsString(args, i), this.dateProvider);
        }

        public QueryTemplateBuilderWithRequest dateAtPosition(int i) {
            return new QueryTemplateBuilderWithRequest(this.queryWrapperProvider, this.codeProvider, args -> QueryTemplate.resolveAsOptionalDate(args, i));
        }
    }
}
