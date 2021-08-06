package ir.sample.app.BeheshtRay.database;


import ir.sample.app.BeheshtRay.models.Faculty;
import ir.sample.app.BeheshtRay.models.Feedback;
import ir.sample.app.BeheshtRay.models.Student;
import ir.sample.app.BeheshtRay.models.Teacher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;

public class DbOperation {


    /**
     * Send the data of new registered users to database
     * TABLE: STUDENT
     *
     * @param student
     * @param connection
     */
    public static void sendUserInfo(Student student, Connection connection) {
        try {
            String checkSql = "INSERT INTO student(first_name, last_name, student_id, faculty_id, user_id, gender, photo_url, karma) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, student.getStudentFirstName());
            pstmt.setString(2, student.getStudentLastName());
            pstmt.setString(3, student.getStudentId());
            pstmt.setInt(4, student.getStudentFacultyId());
            pstmt.setString(5, student.getUserId());
            pstmt.setString(6, student.getStudentGender());
            pstmt.setString(7, student.getStudentPhotoURL());
            pstmt.setString(8, student.getUserKarma());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return ID of the selected faculty by getting its name
     * TABLE: FACULTY
     *
     * @param faculty_name
     * @param connection
     * @return
     */
    public static int retrieveFacultyIdByName(String faculty_name, Connection connection) {
        String checkSql = "SELECT faculty_id FROM faculty WHERE faculty_name=?";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, faculty_name);
            ResultSet resultSet = pstmt.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public static ArrayList<Student> retrieveStudentByUserId(String user_id, Connection connection) {
        String checkSql = "SELECT *, faculty_name FROM student INNER JOIN faculty ON student.faculty_id = faculty.faculty_id WHERE user_id=?";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, user_id);
            ResultSet resultSet = pstmt.executeQuery();
            resultSet.next();

            ArrayList<Student> students = new ArrayList<>();
            Student student = new Student(user_id);

            student.setStudentFirstName(resultSet.getString(1));
            student.setStudentLastName(resultSet.getString(2));
            student.setStudentId(resultSet.getString(3));
            student.setStudentFacultyId(resultSet.getInt(4));
            student.setStudentGender(resultSet.getString(6));
            student.setStudentPhotoURL(resultSet.getString(7));
            student.setUserKarma(resultSet.getString(8));
            student.setStudentFacultyName(resultSet.getString(9));
            students.add(student);

            return students;


        } catch (SQLException throwables) {
            return null;
        }
    }


    public static ArrayList<Teacher> retrieveTheMostFamousTeachers(int facultyId, boolean isLimited, Connection connection) {
        try {
            String postfix = isLimited ? " limit 5" : " limit 20";

            String checkSql = "SELECT teacher_name, photo_url, (AVG(score_1) + AVG(score_2) + AVG(score_3) + AVG(score_4))/4.0 AS over_all_average FROM teacher INNER JOIN feedback f ON teacher.teaching_id = f.teaching_id WHERE teacher.faculty_id=? GROUP BY teacher_name, photo_url ORDER BY over_all_average DESC" + postfix;
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setInt(1, facultyId);
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeacherName(resultSet.getString(1));
                teacher.setPhotoURL(resultSet.getString(2));
                teachers.add(teacher);
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Feedback> retrieveTheMostVotedFeedbacks(int facultyId, boolean isLimited, Connection connection) {
        try {
            String postfix = isLimited ? " limit 3" : " limit 20";

            String checkSql = "SELECT f.up_votes, f.down_votes, f.persian_date, f.student_score, t.lesson_name, t.teacher_name,f.extended_feedback, (f.up_votes-f.down_votes) AS diff FROM feedback f INNER JOIN teacher t ON t.teaching_id=f.teaching_id WHERE t.faculty_id=? AND extended_feedback IS NOT NULL ORDER BY diff DESC" + postfix;
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setInt(1, facultyId);
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
                feedback.setUpVotes(resultSet.getInt(1));
                feedback.setDownVotes(resultSet.getInt(2));
                feedback.setPersianDate(resultSet.getString(3));
                feedback.setStudentScore(resultSet.getString(4));
                feedback.setLessonName(resultSet.getString(5));
                feedback.setTeacherName(resultSet.getString(6));
                feedback.setExtendedFeedback(resultSet.getString(7));
                feedbacks.add(feedback);
            }
            return feedbacks;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Teacher> retrieveTeachersList(String userID, boolean isLimited, Connection connection) {
        try {

            String postfix = isLimited ? " limit 3" : "";

            String checkSql = "SELECT teaching_id, teacher_name, lesson_name, t.photo_url FROM teacher t INNER JOIN student s ON t.faculty_id = s.faculty_id WHERE user_id=?" + postfix;
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, userID);
            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeachingId(resultSet.getInt(1));
                teacher.setTeacherName(resultSet.getString(2));
                teacher.setLessonName(resultSet.getString(3));
                teacher.setPhotoURL(resultSet.getString(4));
                teachers.add(teacher);
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }

    }


    public static ArrayList<Faculty> retrieveFacultyByUserId(String user_id, Connection connection) {
        String checkSql = "SELECT f.faculty_name, f.photo_url FROM faculty f INNER JOIN student s on f.faculty_id = s.faculty_id WHERE user_id=?";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, user_id);
            ResultSet resultSet = pstmt.executeQuery();
            resultSet.next();

            ArrayList<Faculty> faculties  = new ArrayList<>();
            Faculty faculty = new Faculty();

            faculty.setFacultyName(resultSet.getString(1));
            faculty.setPhotoURL(resultSet.getString(2));

            faculties.add(faculty);

            return faculties;


        } catch (SQLException throwables) {
            return null;
        }
    }


    public static ArrayList<Feedback> retrieveMyFeedbacks(String userID, boolean isExtended, Connection connection) {
        try {

            String checkSql = isExtended ? "SELECT feedback_id, persian_date, student_score, t.lesson_name, t.teacher_name, up_votes, down_votes, extended_feedback, (score_1+score_2+score_3+score_4)/4.0 AS average FROM feedback f INNER JOIN student s ON f.user_id = s.user_id INNER JOIN teacher t ON f.teaching_id = t.teaching_id WHERE f.user_id = ? AND f.extended_feedback IS NOT NULL"
                    : "SELECT feedback_id, persian_date, student_score, t.lesson_name, t.teacher_name, (score_1+score_2+score_3+score_4)/4.0 AS average FROM feedback f INNER JOIN student s ON f.user_id = s.user_id INNER JOIN teacher t ON f.teaching_id = t.teaching_id WHERE f.user_id = ? AND f.extended_feedback IS NULL";

            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, userID);
            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();

                if (isExtended){
                    feedback.setFeedbackId(resultSet.getInt(1));
                    feedback.setPersianDate(resultSet.getString(2));
                    feedback.setStudentScore(resultSet.getString(3));
                    feedback.setLessonName(resultSet.getString(4));
                    feedback.setTeacherName(resultSet.getString(5));
                    feedback.setUpVotes(resultSet.getInt(6));
                    feedback.setDownVotes(resultSet.getInt(7));
                    feedback.setExtendedFeedback(resultSet.getString(8));
                    feedback.setAverageScore(resultSet.getDouble(9));


                }else {
                    feedback.setFeedbackId(resultSet.getInt(1));
                    feedback.setPersianDate(resultSet.getString(2));
                    feedback.setStudentScore(resultSet.getString(3));
                    feedback.setLessonName(resultSet.getString(4));
                    feedback.setTeacherName(resultSet.getString(5));
                    feedback.setAverageScore(resultSet.getDouble(6));

                }
                System.out.println(feedback);
                feedbacks.add(feedback);
            }
            return feedbacks;
        } catch (Exception e) {
            return null;
        }

    }
































































































































































































    /*



    ###########################################################################################################################



     */


    public static void sendFeedBack(Feedback feedback, Connection connection) {
        try {
            String count = "SELECT COUNT(*) FROM feedbacks"; // TODO dbOperation
            PreparedStatement pcount = connection.prepareStatement(count);
            ResultSet rcount = pcount.executeQuery();
            int countnum = 0;
            while (rcount.next()) {
                countnum = Integer.parseInt(rcount.getString(1));
            }
            String checkSql = "INSERT INTO feedbacks(teacher_name, lesson_name, score_1, score_2, score_3, score_4, student_score, extended_feedback, userid, date_number, upvotes, downvotes, feedback_id, created_time, diff_votes, score_avg, is_removed) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,false)";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
//            pstmt.setString(1, feedback.teacher_name);
//            pstmt.setString(2, feedback.lesson_name);
//            pstmt.setDouble(3, feedback.score1);
//            pstmt.setDouble(4, feedback.score2);
//            pstmt.setDouble(5, feedback.score3);
//            pstmt.setDouble(6, feedback.score4);
//            pstmt.setString(7, feedback.student_score);
//            pstmt.setString(8, feedback.extended_feedback);
//            pstmt.setString(9, feedback.user_id);
//            pstmt.setString(10, feedback.date_number);
//            pstmt.setString(11, feedback.upvotes);
//            pstmt.setString(12, feedback.downvotes);
//            pstmt.setString(13, feedback.feedback_id);
//            pstmt.setString(14, feedback.created_time);
//            pstmt.setInt(15, feedback.diff_votes);
//            pstmt.setString(16, feedback.score_avg);
//            pstmt.setString(14, feedback.feedback_id);
//            pstmt.setString(11, String.valueOf(feedback.feedback_key));
            System.out.println("\n\nState:");
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Feedback> retrieveFeedbacksBySelf(String userid, Connection connection) {
        try {
            String checkSql = "SELECT * FROM feedbacks WHERE userid=? AND is_removed=false ORDER BY length(created_time) DESC, created_time DESC";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, userid);
            ResultSet resultSet = pstmt.executeQuery();
//            String data[] = new String[16];
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
//                for (int i = 1; i <= 15; i++) {
//                    data[i] = resultSet.getString(i);
//                }
//                feedback.teacher_name = resultSet.getString(1);
//                feedback.lesson_name = resultSet.getString(2);
//                feedback.score1 = resultSet.getDouble(3);
//                feedback.score2 = resultSet.getDouble(4);
//                feedback.score3 = resultSet.getDouble(5);
//                feedback.score4 = resultSet.getDouble(6);
//                feedback.student_score = resultSet.getString(7);
//                feedback.extended_feedback = resultSet.getString(8);
//                feedback.user_id = resultSet.getString(9);
//                feedback.date_number = resultSet.getString(10);
//                feedback.upvotes = resultSet.getString(11);
//                feedback.downvotes = resultSet.getString(12);
//                feedback.feedback_id = resultSet.getString(13);
//                feedback.created_time = resultSet.getString(14);
//                feedback.diff_votes = resultSet.getInt(15);
//                feedback.score_avg = resultSet.getString(16);
//                feedbacks.add(feedback);

            }
            return feedbacks;
        } catch (Exception e) {
            return null;

        }
    }


    public static ArrayList<Feedback> retrieveFeedbacksBySelf2(String userid, Connection connection) {
        try {
            String checkSql = "SELECT * FROM feedbacks WHERE userid=? AND is_removed=false";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, userid);
            ResultSet resultSet = pstmt.executeQuery();
//            String data[] = new String[15];
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
//                for (int i = 1; i <= 14; i++) {
//                    data[i] = resultSet.getString(i);
//                }
////                feedback.teacher_name = data[1];
////                feedback.lesson_name = data[2];
////                feedback.score1 = data[3];
////                feedback.score2 = data[4];
////                feedback.score3 = data[5];
////                feedback.score4 = data[6];
////                feedback.score_ave = data[7];
////                feedback.student_score = data[8];
////                feedback.extended_feedback = data[9];
////                feedback.user_id = data[10];
////                feedback.date_number = data[11];
////                feedback.upvotes = data[12];
////                feedback.downvotes = data[13];
//                feedback.feedback_id = data[14];
                feedbacks.add(feedback);

            }
            return feedbacks;
        } catch (Exception e) {
            return null;

        }
    }


    public static ArrayList<Feedback> retrieveFeedbacksByFeedbackId(String feedback_id, Connection connection) {
        try {
            String checkSql = "SELECT * FROM feedbacks WHERE feedback_id=? and extended_feedback IS NOT NULL AND is_removed=false";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, feedback_id);
            ResultSet resultSet = pstmt.executeQuery();
//            String data[] = new String[16];
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
//                for (int i = 1; i <= 15; i++) {
//                    data[i] = resultSet.getString(i);
//                }
//                feedback.teacher_name = resultSet.getString(1);
//                feedback.lesson_name = resultSet.getString(2);
//                feedback.score1 = resultSet.getDouble(3);
//                feedback.score2 = resultSet.getDouble(4);
//                feedback.score3 = resultSet.getDouble(5);
//                feedback.score4 = resultSet.getDouble(6);
//                feedback.student_score = resultSet.getString(7);
//                feedback.extended_feedback = resultSet.getString(8);
//                feedback.user_id = resultSet.getString(9);
//                feedback.date_number = resultSet.getString(10);
//                feedback.upvotes = resultSet.getString(11);
//                feedback.downvotes = resultSet.getString(12);
//                feedback.feedback_id = resultSet.getString(13);
//                feedback.created_time = resultSet.getString(14);
//                feedback.diff_votes = resultSet.getInt(15);
//                feedback.score_avg = resultSet.getString(16);

                feedbacks.add(feedback);

            }
            return feedbacks;
        } catch (Exception e) {
            return null;

        }
    }

    public static void updateUpvotes(String feedback_id, int diff_votes, String new_upvote, Connection connection) {
        try {
            String checkSql = "UPDATE feedbacks SET upvotes=?, diff_votes=? WHERE feedback_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, new_upvote);
            pstmt.setInt(2, diff_votes);
            pstmt.setString(3, feedback_id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateDownvotes(String feedback_id, int diff_votes, String new_downvote, Connection connection) {
        try {
            String checkSql = "UPDATE feedbacks SET downvotes=?, diff_votes=? WHERE feedback_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, new_downvote);
            pstmt.setInt(2, diff_votes);
            pstmt.setString(3, feedback_id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Feedback> retrieveFeedbacksByTeacher(String lesson_name, String teacher_name, Connection connection) {
        try {
            String checkSql = "SELECT * FROM feedbacks WHERE teacher_name=? and lesson_name=? and extended_feedback IS NOT NULL AND is_removed=false ORDER BY length(created_time) DESC, created_time DESC";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, teacher_name);
            pstmt.setString(2, lesson_name);
            ResultSet resultSet = pstmt.executeQuery();
//            String data[] = new String[16];
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
//                for (int i = 1; i <= 15; i++) {
//                    data[i] = resultSet.getString(i);
//                }
//                feedback.teacher_name = resultSet.getString(1);
//                feedback.lesson_name = resultSet.getString(2);
//                feedback.score1 = resultSet.getDouble(3);
//                feedback.score2 = resultSet.getDouble(4);
//                feedback.score3 = resultSet.getDouble(5);
//                feedback.score4 = resultSet.getDouble(6);
//                feedback.student_score = resultSet.getString(7);
//                feedback.extended_feedback = resultSet.getString(8);
//                feedback.user_id = resultSet.getString(9);
//                feedback.date_number = resultSet.getString(10);
//                feedback.upvotes = resultSet.getString(11);
//                feedback.downvotes = resultSet.getString(12);
//                feedback.feedback_id = resultSet.getString(13);
//                feedback.created_time = resultSet.getString(14);
//                feedback.diff_votes = resultSet.getInt(15);
//                feedback.score_avg = resultSet.getString(16);

                feedbacks.add(feedback);

            }
            return feedbacks;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Feedback> retrieveFeedbacksByTeacherMostVoted(String lesson_name, String teacher_name, Connection connection) {
        try {
            String checkSql = "SELECT * FROM feedbacks WHERE teacher_name=? and lesson_name=? and extended_feedback IS NOT NULL AND is_removed=false ORDER BY diff_votes DESC";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, teacher_name);
            pstmt.setString(2, lesson_name);
            ResultSet resultSet = pstmt.executeQuery();
//            String data[] = new String[16];
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
//                for (int i = 1; i <= 15; i++) {
//                    data[i] = resultSet.getString(i);
//                }
//                feedback.teacher_name = resultSet.getString(1);
//                feedback.lesson_name = resultSet.getString(2);
//                feedback.score1 = resultSet.getDouble(3);
//                feedback.score2 = resultSet.getDouble(4);
//                feedback.score3 = resultSet.getDouble(5);
//                feedback.score4 = resultSet.getDouble(6);
//                feedback.student_score = resultSet.getString(7);
//                feedback.extended_feedback = resultSet.getString(8);
//                feedback.user_id = resultSet.getString(9);
//                feedback.date_number = resultSet.getString(10);
//                feedback.upvotes = resultSet.getString(11);
//                feedback.downvotes = resultSet.getString(12);
//                feedback.feedback_id = resultSet.getString(13);
//                feedback.created_time = resultSet.getString(14);
//                feedback.diff_votes = resultSet.getInt(15);
//                feedback.score_avg = resultSet.getString(16);

                feedbacks.add(feedback);

            }
            return feedbacks;
        } catch (Exception e) {
            return null;
        }
    }


//    public static ArrayList<Feedback> retrieveFeedbacksMostVoted(Connection connection) {
//        try {
//            String checkSql = "SELECT * FROM feedbacks WHERE  extended_feedback IS NOT NULL AND is_removed=false ORDER BY diff_votes DESC";
//            PreparedStatement pstmt = connection.prepareStatement(checkSql);
//            ResultSet resultSet = pstmt.executeQuery();
////            String data[] = new String[16];
//            ArrayList<Feedback> feedbacks = new ArrayList<>();
//            int counter = 1;
//            while (resultSet.next() && counter <= 16) {
//                Feedback feedback = new Feedback();
////                for (int i = 1; i <= 15; i++) {
////                    data[i] = resultSet.getString(i);
////                }
////                feedback.teacher_name = resultSet.getString(1);
////                feedback.lesson_name = resultSet.getString(2);
////                feedback.score1 = resultSet.getDouble(3);
////                feedback.score2 = resultSet.getDouble(4);
////                feedback.score3 = resultSet.getDouble(5);
////                feedback.score4 = resultSet.getDouble(6);
////                feedback.student_score = resultSet.getString(7);
////                feedback.extended_feedback = resultSet.getString(8);
////                feedback.user_id = resultSet.getString(9);
////                feedback.date_number = resultSet.getString(10);
////                feedback.upvotes = resultSet.getString(11);
////                feedback.downvotes = resultSet.getString(12);
////                feedback.feedback_id = resultSet.getString(13);
////                feedback.created_time = resultSet.getString(14);
////                feedback.diff_votes = resultSet.getInt(15);
////                feedback.score_avg = resultSet.getString(16);
//
//                feedbacks.add(feedback);
//                counter++;
//
//
//            }
//            return feedbacks;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public static ArrayList<Feedback> retrieveFeedbacksLeastVoted(Connection connection) {
        try {
            String checkSql = "SELECT * FROM feedbacks WHERE  extended_feedback IS NOT NULL AND is_removed=false ORDER BY diff_votes";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            ResultSet resultSet = pstmt.executeQuery();
//            String data[] = new String[16];
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            int counter = 1;
            while (resultSet.next() && counter <= 10) {
                Feedback feedback = new Feedback();
//                for (int i = 1; i <= 15; i++) {
//                    data[i] = resultSet.getString(i);
//                }
//                feedback.teacher_name = resultSet.getString(1);
//                feedback.lesson_name = resultSet.getString(2);
//                feedback.score1 = resultSet.getDouble(3);
//                feedback.score2 = resultSet.getDouble(4);
//                feedback.score3 = resultSet.getDouble(5);
//                feedback.score4 = resultSet.getDouble(6);
//                feedback.student_score = resultSet.getString(7);
//                feedback.extended_feedback = resultSet.getString(8);
//                feedback.user_id = resultSet.getString(9);
//                feedback.date_number = resultSet.getString(10);
//                feedback.upvotes = resultSet.getString(11);
//                feedback.downvotes = resultSet.getString(12);
//                feedback.feedback_id = resultSet.getString(13);
//                feedback.created_time = resultSet.getString(14);
//                feedback.diff_votes = resultSet.getInt(15);
//                feedback.score_avg = resultSet.getString(16);

                feedbacks.add(feedback);
                counter++;


            }
            return feedbacks;
        } catch (Exception e) {
            return null;
        }
    }


//    public static ArrayList<Teacher> retrieveTeachers(Connection connection) {
//        try {
//            String checkSql = "SELECT * FROM teachers ORDER BY teacher_name";
//            PreparedStatement pstmt = connection.prepareStatement(checkSql);
//            ResultSet resultSet = pstmt.executeQuery();
////            System.out.println(resultSet.next());
////            System.out.println(resultSet.next());
////            System.out.println(resultSet.next());
////            System.out.println(resultSet.next());
////            System.out.println(resultSet.next());
//            String[] data = new String[12];
//            ArrayList<Teacher> teachers = new ArrayList<>();
//            while (resultSet.next()) {
////                System.out.println("Hello");
//                Teacher teacher = new Teacher();
//                for (int i = 1; i <= 11; i++) {
//                    data[i] = resultSet.getString(i);
//                }
////                System.out.println("data: " + data[1]);
////                teacher.teacher_name = data[1];
////                teacher.lesson_name = data[2];
////                teacher.teacher_email = data[3];
////                teacher.teacher_academic_group = data[4];
////                teacher.teacher_key = data[10];
////                teacher.teacher_photo = data[11];
////                System.out.println(teacher.teacher_name);
//                teachers.add(teacher);
//            }
//            return teachers;
//        } catch (Exception e) {
//            return null;
//        }
//
//    }


    public static ArrayList<Teacher> retrieveTeacherByKey(String teacher_key, Connection connection) {
        try {
            String checkSql = "SELECT * FROM teachers WHERE teacher_key=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, teacher_key);
            ResultSet resultSet = pstmt.executeQuery();
            String[] data = new String[12];
            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                System.out.println("Hello");
                Teacher teacher = new Teacher();
                for (int i = 1; i <= 11; i++) {
                    data[i] = resultSet.getString(i);
                }
//                System.out.println("data: " + data[1]);
//                teacher.teacher_name = data[1];
//                teacher.lesson_name = data[2];
//                teacher.teacher_email = data[3];
//                teacher.teacher_academic_group = data[4];
//                teacher.teacher_key = data[10];
//                teacher.teacher_photo = data[11];
                teachers.add(teacher);
//                System.out.println(teacher.teacher_name);
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }

    }


    public static ArrayList<Teacher> retrieveTeacherByNameAndLesson(String teacher_name, String lesson_name, Connection connection) {
        try {
            String checkSql = "SELECT * FROM teachers WHERE teacher_name=? AND lesson_name=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, teacher_name);
            pstmt.setString(2, lesson_name);
            ResultSet resultSet = pstmt.executeQuery();
            String[] data = new String[12];
            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                System.out.println("Hello");
                Teacher teacher = new Teacher();
                for (int i = 1; i <= 11; i++) {
                    data[i] = resultSet.getString(i);
                }
//                System.out.println("data: " + data[1]);
//                teacher.teacher_name = data[1];
//                teacher.lesson_name = data[2];
//                teacher.teacher_email = data[3];
//                teacher.teacher_academic_group = data[4];
//                teacher.teacher_key = data[10];
//                teacher.teacher_photo = data[11];
                teachers.add(teacher);
//                System.out.println(teacher.teacher_name);
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }

    }

    public static ArrayList<Teacher> retrieveLessonsByTeacher(String teacher_name, Connection connection) {
        try {
            String checkSql = "SELECT * FROM teachers WHERE teacher_name=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, teacher_name);
            ResultSet resultSet = pstmt.executeQuery();
            String[] data = new String[12];
            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                System.out.println("Hello");
                Teacher teacher = new Teacher();
                for (int i = 1; i <= 11; i++) {
                    data[i] = resultSet.getString(i);
                }
////                System.out.println("data: " + data[1]);
//                teacher.teacher_name = data[1];
//                teacher.lesson_name = data[2];
//                teacher.teacher_email = data[3];
//                teacher.teacher_academic_group = data[4];
//                teacher.teacher_key = data[10];
//                teacher.teacher_photo = data[11];
                teachers.add(teacher);
//                System.out.println(teacher.teacher_name);
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }

    }


    public static ArrayList<Teacher> search(String search_input, Connection connection) {
        try {

            String checkSql = "SELECT * FROM teachers WHERE teacher_name LIKE ? OR lesson_name LIKE ? OR teacher_email iLIKE ?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, '%' + search_input + '%');
            pstmt.setString(2, '%' + search_input + '%');
            pstmt.setString(3, '%' + search_input + '%');
            ResultSet resultSet = pstmt.executeQuery();
            String[] data = new String[12];
            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                System.out.println("Hello");
                Teacher teacher = new Teacher();
                for (int i = 1; i <= 11; i++) {
                    data[i] = resultSet.getString(i);
                }
//                System.out.println("data: " + data[1]);
//                teacher.teacher_name = data[1];
//                teacher.lesson_name = data[2];
//                teacher.teacher_email = data[3];
//                teacher.teacher_academic_group = data[4];
//                teacher.teacher_key = data[10];
//                teacher.teacher_photo = data[11];
                teachers.add(teacher);
//                System.out.println(teacher.teacher_name);
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }

    }


    public static String retrieveTeacherURLImage(String teacher_name, Connection connection) {
        try {
            String checkSql = "SELECT * FROM teachers WHERE teacher_name=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, teacher_name);
            ResultSet resultSet = pstmt.executeQuery();
            resultSet.next();
            return resultSet.getString(11);
        } catch (Exception e) {
            return null;
        }
    }


    public static String retrieveTeacherID(String teacher_name, String lesson_name, Connection connection) {
        try {
            String checkSql = "SELECT * FROM teachers WHERE teacher_name=? AND lesson_name=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, teacher_name);
            pstmt.setString(2, lesson_name);
            ResultSet resultSet = pstmt.executeQuery();
            resultSet.next();
            return resultSet.getString(10);
        } catch (Exception e) {
            return null;
        }
    }


    public static void sendUserInfo2(Student student, Connection connection) {
        try {
            String count = "SELECT COUNT(*) FROM students"; // TODO dbOperation
            PreparedStatement pcount = connection.prepareStatement(count);
            ResultSet rcount = pcount.executeQuery();
            int countnum = 0;
            while (rcount.next()) {
                countnum = Integer.parseInt(rcount.getString(1));
            }
            String checkSql = "INSERT INTO students(student_name, student_id, student_faculty, student_gender, student_photo, student_upvotes, student_downvotes, user_id) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
//            pstmt.setString(1, student.student_name);
//            pstmt.setString(2, student.student_id);
//            pstmt.setString(3, student.student_faculty);
//            pstmt.setString(4, student.student_gender);
//            pstmt.setString(5, student.student_photo);
//            pstmt.setString(6, student.student_upvotes);
//            pstmt.setString(7, student.student_downvotes);
//            pstmt.setString(8, student.user_id);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Student> retrieveVoteStatus(String user_id, Connection connection) {
        try {
            String checkSql = "SELECT * FROM students WHERE user_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, user_id);
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[9];
            ArrayList<Student> students = new ArrayList<>();
            while (resultSet.next()) {
//                Student student = new Student();
                for (int i = 1; i <= 8; i++) {
                    data[i] = resultSet.getString(i);
                }

//                student.student_name = data[1];
//                student.student_id = data[2];
//                student.student_faculty = data[3];
//                student.student_gender = data[4];
//                student.student_photo = data[5];
//                student.student_upvotes = data[6];
//                student.student_downvotes = data[7];
//                student.user_id = data[8];

//                students.add(student);

            }
            return students;
        } catch (Exception e) {
            return null;

        }

    }


    public static void updateUpvotesListForUser(String user_id, String student_upvotes, Connection connection) {
        try {
            String checkSql = "UPDATE students SET student_upvotes=? WHERE user_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, student_upvotes);
            pstmt.setString(2, user_id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateDownvotesListForUser(String user_id, String student_downvotes, Connection connection) {
        try {
            String checkSql = "UPDATE students SET student_downvotes=? WHERE user_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, student_downvotes);
            pstmt.setString(2, user_id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteExtendedVote(String feedback_id, Connection connection) {
        try {
            String checkSql = "UPDATE feedbacks SET extended_feedback=NULL, diff_votes=0, score_1=0, score_2=0, score_3=0, score_4=0, score_avg='0', is_removed=true WHERE feedback_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, feedback_id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Teacher> retrieveLessonsByTeacherNotRepetitive(String teacher_name, String lesson_name, Connection connection) {
        try {
            String checkSql = "SELECT * FROM teachers WHERE teacher_name=? AND lesson_name!=? ";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, teacher_name);
            pstmt.setString(2, lesson_name);
            ResultSet resultSet = pstmt.executeQuery();
            String[] data = new String[12];
            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                System.out.println("Hello");
                Teacher teacher = new Teacher();
                for (int i = 1; i <= 11; i++) {
                    data[i] = resultSet.getString(i);
                }
//                System.out.println("data: " + data[1]);
//                teacher.teacher_name = data[1];
//                teacher.lesson_name = data[2];
//                teacher.teacher_email = data[3];
//                teacher.teacher_academic_group = data[4];
//                teacher.teacher_key = data[10];
//                teacher.teacher_photo = data[11];
                teachers.add(teacher);
//                System.out.println(teacher.teacher_name);
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }

    }


    public static ArrayList<Double> retrieveScoreMagic(String teacher_name, String lesson_name, Connection connection) {
        try {
            String checkSql = "SELECT (AVG(score_1) + AVG(score_2) + AVG(score_3) + AVG(score_4))/4.0, AVG(score_1), AVG(score_2), AVG(score_3), AVG(score_4) FROM feedbacks WHERE score_1 IS NOT NULL AND score_2 IS NOT NULL AND score_3 IS NOT NULL AND score_4 IS NOT NULL AND teacher_name=? AND lesson_name=? AND is_removed=false";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, teacher_name);
            pstmt.setString(2, lesson_name);
            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Double> magicScores = new ArrayList<>();
            if (resultSet.next()) {
                magicScores.add(resultSet.getDouble(1));
                magicScores.add(resultSet.getDouble(2));
                magicScores.add(resultSet.getDouble(3));
                magicScores.add(resultSet.getDouble(4));
                magicScores.add(resultSet.getDouble(5));
                return magicScores;
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }


    public static String convertToEnglishDigits(String value) {
        return value.replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹")
                .replace("0", "۰");
    }

    public static ArrayList<Teacher> retrieveByTaTeam(Connection connection) {
        try {
            int counter = 0;
            String checkSql = "select teacher_name, lesson_name, AVG(score_3)  as team_ta from feedbacks group by (teacher_name, lesson_name) order by team_ta DESC";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next() && counter <= 7) {
                Teacher teacher = new Teacher();
//                teacher.teacher_name = resultSet.getString(1);
//                teacher.lesson_name = resultSet.getString(2);
//                teacher.tempAverage = convertToEnglishDigits(BigDecimal.valueOf(resultSet.getDouble(3)).setScale(2, RoundingMode.HALF_UP).toString());
//                teacher.teacher_photo = retrieveTeacherURLImage(teacher.teacher_name, connection);
//                teacher.teacher_key = retrieveTeacherID(teacher.teacher_name, teacher.lesson_name, connection);
                teachers.add(teacher);
                counter++;
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }
    }

//    public static ArrayList<Teacher> retrieveTheMostFamousTeachers(Connection connection) {
//        try {
//            int counter = 1;
//            ArrayList<Teacher> teachers = new ArrayList<>();
//            String checkSql = "select teacher_name, (AVG(score_1) + AVG(score_2) + AVG(score_3) + AVG(score_4))/4.0 as team_score from feedbacks group by teacher_name order by team_score DESC";
//            PreparedStatement pstmt = connection.prepareStatement(checkSql);
//            ResultSet resultSet = pstmt.executeQuery();
//            String[] data = new String[3];
//            while (resultSet.next() && counter <= 5) {
//                Teacher teacher = new Teacher();
////                teacher.teacher_name = resultSet.getString(1);
////                System.out.println(teacher.teacher_name);
////                teacher.teacher_photo = retrieveTeacherURLImage(teacher.teacher_name, connection);
////                System.out.println(teacher.teacher_photo);
//                teachers.add(teacher);
//                counter++;
//            }
//            return teachers;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public static ArrayList<Teacher> retrieveTheMostFamousTeachers2(Connection connection) {
        try {
            int counter = 1;
            ArrayList<Teacher> teachers = new ArrayList<>();
            String checkSql = "select teacher_name, (AVG(score_1) + AVG(score_2) + AVG(score_3) + AVG(score_4))/4.0 as team_score from feedbacks group by teacher_name order by team_score DESC";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            ResultSet resultSet = pstmt.executeQuery();
            String[] data = new String[3];
            while (resultSet.next() && counter <= 20) {
                Teacher teacher = new Teacher();
//                teacher.teacher_name = resultSet.getString(1);
//                System.out.println(teacher.teacher_name);
//                teacher.teacher_photo = retrieveTeacherURLImage(teacher.teacher_name, connection);
//                System.out.println(teacher.teacher_photo);
                teachers.add(teacher);
                counter++;
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }
    }


    public static ArrayList<Teacher> retrieveTheMostFamousTeachersLessons(Connection connection) {
        try {
            int counter = 1;
            ArrayList<Teacher> teachers = new ArrayList<>();
            String checkSql = "select teacher_name, lesson_name, (AVG(score_1) + AVG(score_2) + AVG(score_3) + AVG(score_4))/4.0 as team_score from feedbacks group by (teacher_name, lesson_name) order by team_score DESC";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next() && counter <= 7) {
                Teacher teacher = new Teacher();
//                teacher.teacher_name = resultSet.getString(1);
//                teacher.lesson_name = resultSet.getString(2);
//                teacher.tempAverage = convertToEnglishDigits(BigDecimal.valueOf(resultSet.getInt(3)).setScale(2, RoundingMode.HALF_UP).toString());
//                System.out.println(teacher.teacher_name);
//                teacher.teacher_photo = retrieveTeacherURLImage(teacher.teacher_name, connection);
//                teacher.teacher_key = retrieveTeacherID(teacher.teacher_name, teacher.lesson_name, connection);
//                System.out.println(teacher.teacher_photo);
                teachers.add(teacher);
                counter++;
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }
    }


    public static ArrayList<Teacher> retrieveMostCommentedTeachers(Connection connection) {
        try {
            int counter = 1;
            ArrayList<Teacher> teachers = new ArrayList<>();
            String checkSql = "select teacher_name, lesson_name, COUNT(*) as comment_count from feedbacks group by (teacher_name, lesson_name) order by comment_count DESC";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next() && counter <= 7) {
                Teacher teacher = new Teacher();
//                teacher.teacher_name = resultSet.getString(1);
//                teacher.lesson_name = resultSet.getString(2);
//                teacher.tempAverage = convertToEnglishDigits(String.valueOf(resultSet.getInt(3)));
//
//                System.out.println(teacher.teacher_name);
//                teacher.teacher_photo = retrieveTeacherURLImage(teacher.teacher_name, connection);
//                teacher.teacher_key = retrieveTeacherID(teacher.teacher_name, teacher.lesson_name, connection);
//                System.out.println(teacher.teacher_photo);
                teachers.add(teacher);
                counter++;
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }
    }


    public static ArrayList<Teacher> retrieveLeastCommentedTeachers(Connection connection) {
        try {
            int counter = 1;
            ArrayList<Teacher> teachers = new ArrayList<>();
            String checkSql = "select teacher_name, lesson_name, COUNT(*) as comment_count from feedbacks group by (teacher_name, lesson_name) order by comment_count";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next() && counter <= 7) {
                Teacher teacher = new Teacher();
//                teacher.teacher_name = resultSet.getString(1);
//                teacher.lesson_name = resultSet.getString(2);
//                teacher.tempAverage = convertToEnglishDigits(String.valueOf(resultSet.getInt(3)));
//
//                System.out.println(teacher.teacher_name);
//                teacher.teacher_photo = retrieveTeacherURLImage(teacher.teacher_name, connection);
//                teacher.teacher_key = retrieveTeacherID(teacher.teacher_name, teacher.lesson_name, connection);
//                System.out.println(teacher.teacher_photo);
                teachers.add(teacher);
                counter++;
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }
    }

}
