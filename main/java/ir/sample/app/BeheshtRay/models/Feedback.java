package ir.sample.app.BeheshtRay.models;

public class Feedback {
    private int teachingId;
    private String userId;

    private double score1;
    private double score2;
    private double score3;
    private double score4;

    private String studentScore;
    private String extendedFeedback;

    private String persianDate;

    private int upVotes;
    private int downVotes;

    private int feedbackId;



    public Feedback(int teachingId, String userId, double score1, double score2, double score3, double score4, String studentScore, String extendedFeedback, String persianDate) {
        this.teachingId = teachingId;
        this.userId = userId;
        this.score1 = score1;
        this.score2 = score2;
        this.score3 = score3;
        this.score4 = score4;
        this.studentScore = studentScore;
        this.extendedFeedback = extendedFeedback;
        this.persianDate = persianDate;
    }

    public Feedback() {

    }

    public int getTeachingId() {
        return teachingId;
    }

    public String getUserId() {
        return userId;
    }

    public double getScore1() {
        return score1;
    }

    public double getScore2() {
        return score2;
    }

    public double getScore3() {
        return score3;
    }

    public double getScore4() {
        return score4;
    }

    public String getStudentScore() {
        return studentScore;
    }

    public String getExtendedFeedback() {
        return extendedFeedback;
    }

    public String getPersianDate() {
        return persianDate;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public int getFeedbackId() {
        return feedbackId;
    }


    public void setTeachingId(int teachingId) {
        this.teachingId = teachingId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setScore1(double score1) {
        this.score1 = score1;
    }

    public void setScore2(double score2) {
        this.score2 = score2;
    }

    public void setScore3(double score3) {
        this.score3 = score3;
    }

    public void setScore4(double score4) {
        this.score4 = score4;
    }

    public void setStudentScore(String studentScore) {
        this.studentScore = studentScore;
    }

    public void setExtendedFeedback(String extendedFeedback) {
        this.extendedFeedback = extendedFeedback;
    }

    public void setPersianDate(String persianDate) {
        this.persianDate = persianDate;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }


    private String lessonName;
    private String teacherName;

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getLessonName() {
        return lessonName;
    }

    public String getTeacherName() {
        return teacherName;
    }


    private double averageScore;

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public double getAverageScore() {
        return averageScore;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "teachingId=" + teachingId +
                ", userId='" + userId + '\'' +
                ", score1=" + score1 +
                ", score2=" + score2 +
                ", score3=" + score3 +
                ", score4=" + score4 +
                ", studentScore='" + studentScore + '\'' +
                ", extendedFeedback='" + extendedFeedback + '\'' +
                ", persianDate='" + persianDate + '\'' +
                ", upVotes=" + upVotes +
                ", downVotes=" + downVotes +
                ", feedbackId=" + feedbackId +
                ", lessonName='" + lessonName + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", averageScore=" + averageScore +
                '}';
    }
}