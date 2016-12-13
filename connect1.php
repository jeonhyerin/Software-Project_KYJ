<?php
	$conn = mysqli_connect("localhost", "root", "autoset");

	if(mysqli_connect_errno($conn))
	{
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}
	
	mysqli_set_charset($conn, "utf-8");
	mysqli_select_db($conn, "db");

	$res = mysqli_query($conn, "select * from gps");

	$result = array();

	while($row = mysqli_fetch_array($res)) {
		array_push($result, array('user_id'=>$row[0], 'user_latitude'=>$row[1], 'user_longitude'=>$row[2]));
	} 

	echo json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);

	mysqli_close($conn);
?>
