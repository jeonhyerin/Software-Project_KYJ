CREATE TABLE gps(
  id int(11) primary key auto_increment,
  user_latitude char(30) not null,
  user_longitude char(30) not null,
  user_email char(30) not null,
  password char(30) not null,
  time datetime not null
  );
