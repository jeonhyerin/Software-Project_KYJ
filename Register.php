<?php
	$conn = mysqli_connect("localhost", "root", "autoset");

	if(mysqli_connect_errno($conn))
	{
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}
	
	mysqli_set_charset($conn, "utf-8");
	mysqli_select_db($conn, "db");

	$uid = $_POST['user_id'];
	$name = $_POST['name'];
	$email = $_POST['email'];
	$password = $_POST['password'];

	$res = mysqli_query($conn, "INSERT INTO login(user_id, name, email, password, created_at) VALUES('$uid', '$name', '$email', '$password', NOW())");
	
	if($res)
		echo "Success";

	else
		echo "Fail";


	mysqli_close($conn);
?>
