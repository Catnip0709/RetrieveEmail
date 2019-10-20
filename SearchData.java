import java.sql.*;
import java.util.Scanner;
import java.util.Vector;

public class SearchData {
    // MySQL - JDBC驱动名及数据库、URL、用户、密码
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root";

    static Vector<String> SearchMysqlInfo(String type, String target){
        Connection conn = null;
        Statement stmt  = null;
        Vector<String> result = new Vector();
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
            // 打开链接
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            // 执行sql语句
            stmt = conn.createStatement();
            String sql = "SELECT Path FROM Email WHERE " + type + " LIKE \"%" + target + "%\";";
            ResultSet rs = stmt.executeQuery(sql); // 执行sql
            rs.last();               // 移到最后一行
            int count = rs.getRow(); // 计数
            rs.beforeFirst();        // 移到初始位置
            result = new Vector (count);

            // 展开结果集数据库
            while(rs.next()){
                result.add(rs.getString("Path"));
            }
            // 完成后关闭
            stmt.close();
            conn.close();
        } catch(Exception se) {
            se.printStackTrace();// 处理 JDBC 错误
        }
        finally{
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException ignored) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return result;
    }

    static void PrintPath(Vector<String> SearchResult, int page, int groupNum){
        int cur = (page - 1) * 10;
        for(int i = 0; i < 10; i++){
            if(cur >= SearchResult.size())
                break;
            System.out.println(cur + ": " + SearchResult.get(cur));
            cur++;
        }
    }

    static void ShowResult(Vector<String> SearchResult){
        int num = SearchResult.size();
        int group = num / 10 + 1;
        int curPage = 1;

        while(true){
            System.out.println("共 " + num + " 条搜索结果，共 " + group + " 页，当前第 " + curPage + " 页");
            PrintPath(SearchResult, curPage, 10);
            if(curPage == 1) {
                System.out.println("输入 n 进入下一页, 输入 q 退出");
                Scanner input = new Scanner(System.in);
                String UserInput = input.next();
                if (UserInput.equals("n")){
                    curPage++;
                }
                else if (UserInput.equals("q")){
                    break;
                }
                else{
                    System.out.println("输入错误");
                }
            }
            else if(curPage == group){
                System.out.println("输入 l 进入上一页, 输入 q 退出");
                Scanner input = new Scanner(System.in);
                String UserInput = input.next();
                if (UserInput.equals("l")){
                    curPage--;
                }
                else if (UserInput.equals("q")){
                    break;
                }
                else{
                    System.out.println("输入错误");
                }
            }
            else{
                System.out.println("输入 l 进入上一页, 输入 n 进入下一页, 输入 q 退出");
                Scanner input = new Scanner(System.in);
                String UserInput = input.next();
                if(UserInput.equals("l")){
                    curPage--;
                }
                else if(UserInput.equals("n")){
                    curPage++;
                }
                else if (UserInput.equals("q")){
                    break;
                }
                else{
                    System.out.println("输入错误");
                }
            }
        }
    }

    public static void main(String[] args) {
        while(true){
            String type;
            String target;
            while(true) {
                System.out.println("请输入检索类型\n1（标题） 2（发件人） 3（正文） 4（退出）");
                Scanner input = new Scanner(System.in);
                type = input.next();
                boolean flag = true;
                switch (type){
                    case "1": type = "Subject"; break;
                    case "2": type = "From"; break;
                    case "3": type = "Content"; break;
                    case "4": return;
                    default: flag = false;
                }
                if(flag) break;
                else System.out.println("输入错误！");
            }
            System.out.println("请输入检索内容：");
            Scanner input = new Scanner(System.in);
            target = input.next();
            Vector<String> SearchResult = SearchMysqlInfo(type, target);
            ShowResult(SearchResult);
        }
    }
}
