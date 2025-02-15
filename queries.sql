CREATE TABLE TENANT_DB_LIST
(	DATABASE_ID VARCHAR(20) NOT NULL,
     DB_NAME VARCHAR(200) NOT NULL,
     DB_PASSWORD VARCHAR(200) NOT NULL,
     DB_URL VARCHAR(200) NOT NULL,
     CONSTRAINT "TENANT_DB_LIST_PK" PRIMARY KEY ("DATABASE_ID")
);

Insert into TENANT_DB_LIST (DATABASE_ID,DB_NAME,DB_PASSWORD,DB_URL) values ('1001','gaurav','NGaurav@113','TENANT_DB_1');
Insert into TENANT_DB_LIST (DATABASE_ID,DB_NAME,DB_PASSWORD,DB_URL) values ('1002','gaurav','NGaurav@113','TENANT_DB_2');

--------------------------------------------------------------------------------------------------------------------------------------------------------------------------

CREATE TABLE USERS
(	USER_ID VARCHAR(100),
     USER_NAME VARCHAR(200),
     PHONE_NO VARCHAR(200),
     EMAIL_ID VARCHAR(200)
);

Insert into USERS (USER_ID,USER_NAME,PHONE_NO,EMAIL_ID) values ('RD','Rahul Dravid','8790989809','rahul.dravid@bcci.tv');
Insert into USERS (USER_ID,USER_NAME,PHONE_NO,EMAIL_ID) values ('ST','Sachin Tendulkar','9878098765','sachin.tendulkar@bcci.tv');

CREATE TABLE USERS
(	USER_ID VARCHAR(100),
     USER_NAME VARCHAR(200),
     PHONE_NO VARCHAR(200),
     EMAIL_ID VARCHAR(200)
);
Insert into USERS (USER_ID,USER_NAME,PHONE_NO,EMAIL_ID) values ('RP','Ricky Ponting','9900887765','ricky.ponting@ca.au');
Insert into USERS (USER_ID,USER_NAME,PHONE_NO,EMAIL_ID) values ('BL','Brett Lee','6677887766','brett.lee@ca.au');
