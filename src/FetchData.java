import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class FetchData {

	static Connection connection = null;

	public static Connection getConnection() {
		if (connection != null)
			return connection;
		else {
			try {
				Properties prop = new Properties();
				InputStream inputStream = FetchData.class.getClassLoader().getResourceAsStream("db.properties");
				prop.load(inputStream);//грузим данные
				String driver = prop.getProperty("driver");//драйвер к БД
				String url = prop.getProperty("url");//url к БД
				String user = prop.getProperty("user");//имя пользователя БД
				String password = prop.getProperty("password");//пароль к БД
				Class.forName(driver);//загружаем драйвер
				connection = DriverManager.getConnection(url, user, password);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return connection;
		}
	}

	public static ArrayList<ResultData> getData() {//метод считывания всех исходных данных
		ArrayList<ResultData> dataList = new ArrayList<ResultData>();//создаем ArrayList
		ResultData data = null;
		String query1 = "select abs(sum([ИВ1-]) + sum([ИВС-])) as rev_volume from tbl";
		String query2 = "select sum(abs([ИВ1-] + [ИВС-])*[Цена РСВ])/(select abs(sum([ИВ1-]) + sum([ИВС-]))) as rev_prise from tbl";
		String query3 = "select sum(abs([ИВ1-] + [ИВС-])*(IIF ([ИБР]>[Ц заявки], [Ц заявки], [ИБР])))/(select abs(sum([ИВ1-]) + sum([ИВС-]))) as purch_prise from tbl";
		try {
			connection = getConnection();//соединяемся с базой
			Statement statement = connection.createStatement();//посылаем запрос
			ResultSet rs1 = statement.executeQuery(query1);//получаем ответ от базы
			while (rs1.next()) {//пока данные считываются...
				data = new ResultData();//создает объект
				data.setRev_volume(rs1.getDouble("rev_volume"));
				dataList.add(data);//добавляем данные
			}
			ResultSet rs2 = statement.executeQuery(query2);
			while (rs2.next()) {
				data.setRev_prise(rs2.getDouble("rev_prise"));
				dataList.add(data);
			}
			ResultSet rs3 = statement.executeQuery(query3);
			while (rs3.next()) {
				data.setPurch_prise(rs3.getDouble("purch_prise"));
				dataList.add(data);//добавляем данные из таблицы в ArrayList
			}
			for (ResultData d : dataList) {
				System.out.println(d);//выводим данные на консоль
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dataList;//возвращаем ArrayList
	}
}