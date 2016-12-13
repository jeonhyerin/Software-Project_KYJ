<?php
	$conn = mysqli_connect("localhost", "root", "autoset");

	if(mysqli_connect_errno($conn))
	{
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}
	
	mysqli_set_charset($conn, "utf-8");
	mysqli_select_db($conn, "db");
	

		$res = mysqli_query($conn, "SELECT * FROM login");

		$result = array();

		while($row = mysqli_fetch_array($res)) {
			array_push($result, array('email'=>$row[0], 'password'=>$row[1], 'id'=>$row[2], 'name'=>$row[3], 'user_id'=>$row[4], 'created_at'=>$row[5]));
		} 

		echo json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);
	
	mysqli_close($conn);
?>
