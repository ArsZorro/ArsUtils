import org.junit.Test;
import strategy.TextExtractionStore;
import strategy.TextExtractionStrategyBuilder;
import strategy.steps.TokenExtractorStep;
import strategy.steps.PriorityResolvingStep;
import strategy.steps.AdjacentsStep;
import strategy.steps.selectors.ExtractorTokenSelector;
import strategy.steps.selectors.TokenSelector;

public class RegexExtractorTest {

    AdjascentTokensFiltration {
        MergeTokensDetermineType type = MergeTokensType.Adjsscent;
        Integer distance;
    }

    TokenPossibleProperties {
    }

    @Test
    public void strategy() {
        TextExtractionStore
                store = new TextExtractionStore();
        TextExtractionStrategyBuilder strategyBuilder = new TextExtractionStrategyBuilder(store);
        TextExtractionStore store1 = strategyBuilder.addStep(new TokenExtractorStep(new TokenExtractor("extractor_2")))
                                                     .addStep(new PriorityResolvingStep())
                                                     .addStep(new TokenExtractorStep(new TokenExtractor("birthdays_flags"))
                                                     .addStep(new AdjacentsStep(
                                                                     new ExtractorTokenSelector("date_extractor"),
                                                                     new ExtractorTokenSelector("birthdays_flags"),
                                                                     12,
                                                                     )))
                                                     .addStep(new BiSelectTokenStep(
                                                         new TokenSelector("select_2",
                                                             new TokenEntityTypeFiltration(TokenEntityType.PERSON)),
                                                         new TokenSelector(
                                                             "select_1",
                                                             new AdjascentFiltration(
                                                                 new TokenSelector("select_2"), 12),
                                                         new TokenPropertiesTransfering("Дата рождения", "Дата"))
                                                     .addStep(new MergeExtractorsResultStep())
                                                     .addStep()
                                                         .
            ;
        TokeExtractiionStore store2 = strategyBuilder.addExtractors(new TokenExtractor(), new TokenExtractor());
                       strategyBuilder.resolvePriority()
                       .axtractionMediator(ad)
                                      .addExtractionStep(new TokenExtractionStoresMerging(store1, store2, new NerMergingRule()));
    }
}
