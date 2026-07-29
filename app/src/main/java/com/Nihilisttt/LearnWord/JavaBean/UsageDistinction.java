package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsageDistinction extends WordComponent {
    private final String usageDistinctionId;
    private final String distinctionText;
    private final List<String> comparedWordIds;

    public static class Builder {
        private String usageDistinctionId;
        private String wordId;
        private String distinctionText;
        private List<String> comparedWordIds;
        private List<String> kanjiComponents;
        private List<String> kanaComponents;

        public Builder usageDistinctionId(String id) { this.usageDistinctionId = id; return this; }
        public Builder wordId(String wordId) { this.wordId = wordId; return this; }
        public Builder distinctionText(String text) { this.distinctionText = text; return this; }
        public Builder comparedWordIds(List<String> ids) { this.comparedWordIds = new ArrayList<>(ids); return this; }
        public Builder kanjiComponents(List<String> components) { this.kanjiComponents = new ArrayList<>(components); return this; }
        public Builder kanaComponents(List<String> components) { this.kanaComponents = new ArrayList<>(components); return this; }

        public UsageDistinction build() {
            return new UsageDistinction(
                    usageDistinctionId, wordId, distinctionText,
                    comparedWordIds != null ? Collections.unmodifiableList(comparedWordIds) : Collections.emptyList(),
                    unmodifiableListOf(kanjiComponents), unmodifiableListOf(kanaComponents)
            );
        }
    }

    private UsageDistinction(String usageDistinctionId, String wordId, String distinctionText,
                             List<String> comparedWordIds, List<String> kanjiComponents, List<String> kanaComponents) {
        super(wordId, kanjiComponents, kanaComponents);
        this.usageDistinctionId = usageDistinctionId;
        this.distinctionText = distinctionText;
        this.comparedWordIds = comparedWordIds;
    }

    public String getUsageDistinctionId() { return usageDistinctionId; }
    public String getDistinctionText() { return distinctionText; }
    public List<String> getComparedWordIds() { return comparedWordIds; }

    @Override
    public String toString() {
        return distinctionText;
    }
}
