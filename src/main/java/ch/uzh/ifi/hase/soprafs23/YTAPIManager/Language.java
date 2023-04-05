package ch.uzh.ifi.hase.soprafs23.YTAPIManager;

public enum Language {
    ENGLISH("en"), GERMAN("de"), CHINESE("zh-Hans"); // zh-Hans simplified chinese, zh-Hant traditional chinese

    private String ISO_639_1_Code;

    Language(String ISO_639_1_Code) {
        this.ISO_639_1_Code = ISO_639_1_Code;
    }

    public String getISO_639_1_Code() {
        return ISO_639_1_Code;
    }
}
