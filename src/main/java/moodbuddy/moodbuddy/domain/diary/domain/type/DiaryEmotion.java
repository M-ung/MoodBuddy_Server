package moodbuddy.moodbuddy.domain.diary.domain.type;

public enum DiaryEmotion {
    HAPPINESS("H"),
    ANGER("A"),
    DISGUST("D"),
    FEAR("F"),
    NEUTRAL("N"),
    SADNESS("Sa"),
    SURPRISE("Su"),
    FAILED("Fail");

    private final String abbreviation;

    DiaryEmotion(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}