package com.ctb.control.db;

import java.sql.Clob;
import java.sql.SQLException;

import org.apache.beehive.controls.api.bean.ControlExtension;
import org.apache.beehive.controls.system.jdbc.JdbcControl;

import com.ctb.bean.testAdmin.ResponsePoints;
import com.ctb.bean.testAdmin.RosterElement;
import com.ctb.bean.testAdmin.RubricViewData;//Added for rubric view
import com.ctb.bean.testAdmin.ScorableItem;
import com.ctb.bean.testAdmin.TestSession;

@ControlExtension
@JdbcControl.ConnectionDataSource(jndiName = "oasDataSource")
public interface CRScoring extends JdbcControl {
	
	static final long serialVersionUID = 1L;
	
	//score by student: all student list from test admin
	 @JdbcControl.SQL(statement = "select ros.TEST_ROSTER_ID as testRosterId, ros.TEST_ADMIN_ID as testAdminId, tcsc.test_completion_status_desc testCompletionStatusDesc, ros.VALIDATION_STATUS as validationStatus, stu.grade as grade, ros.STUDENT_ID as studentId, ros.CUSTOMER_ID as customerId, ros.SCORING_STATUS as scoringStatus, ros.FORM_ASSIGNMENT as formAssignment, stu.first_name as firstName, stu.middle_name as middleName, stu.last_name as lastName, stu.ext_pin1 as extPin1, stu.user_name as userName, tad.item_set_id  as itemSetIdTC   from test_roster ros, student stu, test_completion_status_code tcsc, test_admin tad  where tad.test_admin_id = {testAdminId}  and tad.test_admin_id = ros.test_admin_id  and stu.student_id = ros.student_id   and tcsc.test_completion_status in ('CO', 'IS', 'IC')  and tcsc.test_completion_status = ros.TEST_COMPLETION_STATUS    and stu.activation_Status = 'AC'  and ros.activation_status = 'AC' and ros.validation_status in ( 'VA', 'PI') ",
             arrayMaxLength = 0)
	RosterElement [] getAllStudentForTestSession(Integer testAdminId) throws SQLException;
	 
	// score by item: all student list from test session and itemsetID
	 @JdbcControl.SQL(statement = "select roster.TEST_ROSTER_ID as testRosterId, roster.TEST_ADMIN_ID as testAdminId, tcsc.test_completion_status_desc testCompletionStatusDesc, stu.grade as grade, roster.STUDENT_ID as studentId, roster.CUSTOMER_ID as customerId, stu.first_name as firstName, stu.middle_name as middleName, stu.last_name as lastName,  stu.ext_pin1 as extPin1,  stu.user_name as userName   from student_item_set_status sis,  test_roster roster, student stu, test_completion_status_code tcsc where sis.item_set_id = {itemSetId} and roster.test_admin_id = {testAdminId} and roster.test_roster_id= sis.test_roster_id and stu.student_id = roster.student_id and tcsc.test_completion_status = roster.TEST_COMPLETION_STATUS and tcsc.test_completion_status in ('CO', 'IS') and stu.activation_status='AC' and roster.activation_status='AC' and roster.validation_status in ( 'VA', 'PI') and sis.validation_status='VA'",
             arrayMaxLength = 0, fetchSize=1000)
	 RosterElement [] getAllStudentForTestSessionAndTD(Integer testAdminId, Integer  itemSetId) throws SQLException;
	 
	//get items from  in score by student
	/* @JdbcControl.SQL(statement = "select iset.item_set_id as itemSetId, iset.item_set_name as itemSetName, isi.item_sort_order as itemSetOrder, isi.item_id as itemId, decode(it.answer_area,null,'CR','AI') as itemType, (select max_points from datapoint dpoint where item_id = isi.item_Id and rownum = 1) as maxPoints ,  (select min_points from datapoint dpoint where item_id = isi.item_Id and rownum = 1) minPoints  from item_set_item  isi, item it, item_set iset, item_set_ancestor isa, student_item_set_status sis where isi.item_set_id = isa.item_set_id and it.item_id = isi.item_id and isa.item_set_type = 'TD' and isa.ancestor_item_set_id = {itemSetId} and sis.test_roster_id = {testRosterId}   and isa.item_set_id = sis.item_set_id and ((upper(it.item_type) = 'CR' and (it.answer_area is null or upper(it.answer_area) = upper('AudioItem')))) and iset.item_set_id = isi.item_set_id order by isi.item_set_id, isi.item_sort_order",
             arrayMaxLength = 1000)
	 ScorableItem [] getAllScorableCRItemsForTestRoster(Integer  testRosterId , Integer  itemSetId) throws SQLException;
	*/
	 
	//Changes for defect #65996
	 @JdbcControl.SQL(statement = "select dpoint.max_points as maxPoints,  dpoint.min_points as minPoints,  dpoint.datapoint_id as dataPointId,  dpoint.item_id as itemId,  nvl(irp.points, 0) as scorePoint,  studentDetails.itemSetId as itemSetId,  studentDetails.itemSetName as itemSetName,  studentDetails.itemSetOrder as itemSetOrder,  studentDetails.itemType as itemType,  decode(nvl(irp.item_response_id, -1), -1, 'Incomplete', 'Complete') as scoreStatus,  decode(nvl(IR.ITEM_RESPONSE_ID, -1),   -1,   'NA',   decode(studentDetails.itemType,    'CR',    (select decode(instr(constructed_response, 'CDATA'), 0, 'NA', 'A') from item_response_cr where item_id = studentDetails.itemId  and test_roster_id = studentDetails.testRosterId  and item_set_id = studentDetails.itemSetId),    (select decode(count(1), 0, 'NA', 'A') from item_response_cr where item_id = studentDetails.itemId  and test_roster_id = studentDetails.testRosterId  and item_set_id = studentDetails.itemSetId  and constructed_response is not null))) as answered   from (select iset.item_set_id as itemSetId,    iset.item_set_name as itemSetName,    isi.item_sort_order as itemSetOrder,    isi.item_id as itemId,    decode(it.answer_area, null, 'CR', 'AI') as itemType,    test_roster_id as testRosterId,    (select item_set_id from datapoint where item_id = isi.item_id and rownum=1) as reItemSetId from item_set_item     isi,    item  it,    item_set    iset,    item_set_ancestor isa,    student_item_set_status sis  where isi.item_set_id = isa.item_set_id and it.item_id = isi.item_id and isa.item_set_type = 'TD' and isa.ancestor_item_set_id = {itemSetId} and sis.test_roster_id = {testRosterId}  and sis.completion_status in ('CO','IS') and sis.validation_status ='VA' and isa.item_set_id = sis.item_set_id and ((upper(it.item_type) = 'CR' and    (it.answer_area is null or    upper(it.answer_area) = upper('AudioItem')))) and iset.item_set_id = isi.item_set_id) studentDetails, ITEM_RESPONSE  IR,  datapoint dpoint,  item_response_points irp  where dpoint.item_id = studentDetails.itemId    and studentDetails.testRosterId = IR.TEST_ROSTER_ID(+)    AND studentDetails.itemSetId = IR.ITEM_SET_ID(+)    AND studentDetails.itemId = IR.ITEM_ID(+)    AND (IR.RESPONSE_SEQ_NUM IS NULL OR  IR.RESPONSE_SEQ_NUM =  (SELECT MAX(IRM.RESPONSE_SEQ_NUM)FROM ITEM_RESPONSE IRM     WHERE studentDetails.testRosterId = IRM.TEST_ROSTER_ID AND studentDetails.itemSetId = IRM.ITEM_SET_ID AND studentDetails.itemId = IRM.ITEM_ID     GROUP BY studentDetails.testRosterId,  studentDetails.itemSetId,  studentDetails.itemId))    and dpoint.item_set_id = studentDetails.reItemSetId    and irp.item_Response_Id(+) = nvl(IR.ITEM_RESPONSE_ID, -1)  order by studentDetails.itemSetId, studentDetails.itemSetOrder",
			 arrayMaxLength = 10000)
	ScorableItem [] getAllScorableCRItemsForTestRoster(Integer  testRosterId , Integer  itemSetId) throws SQLException;

	 
	 
	 //get items from test session in score by item
	 @JdbcControl.SQL(statement = "select iset.item_set_id as itemSetId,iset.item_set_name as itemSetName, isi.item_sort_order as itemSetOrder,  isi.item_id as itemId, decode(it.answer_area,null,'CR','AI') as itemType, it.item_type as testItemType, (select max_points from datapoint dpoint where item_id = isi.item_Id and rownum = 1 ) as maxPoints,  (select min_points from datapoint dpoint where item_id = isi.item_Id and rownum = 1) minPoints   from item_set_item  isi,  item it,  item_set   iset,  item_set_ancestor isa , test_admin tad  where isi.item_set_id = isa.item_set_id  and isa.item_set_type = 'TD'     and  isa.ancestor_item_set_id = tad.item_set_id   and tad.test_admin_id = {testAdminId}  and it.item_id = isi.item_id     and (upper(it.item_type) = 'CR' and  (it.answer_area is null or  upper(it.answer_area) = upper('AudioItem')))     and iset.item_set_id = isi.item_set_id order by isi.item_set_id, isi.item_sort_order",
             arrayMaxLength = 0, fetchSize=1000)
	 ScorableItem [] getAllScorableCRItemsForItemSet(Integer  testAdminId) throws SQLException;
	 
	//get response for AI item
	 @JdbcControl.SQL(statement = "select   isr.constructed_response as constructedResponse from item_response_cr isr where isr.test_roster_id = {testRosterId}  and isr.item_set_id = {deliverableItemId}  and isr.item_id = {itemId}",
			 arrayMaxLength = 0, fetchSize=1000)
	 Clob getAIItemResponse(Integer  testRosterId, Integer  deliverableItemId, String  itemId) throws SQLException;
	 
	 //get response for CR item
	 /*@JdbcControl.SQL(statement =" select fn_getcrresponse({testRosterId},{deliverableItemId},{itemId} ) from dual" )
	 String getCRItemResponse(Integer  testRosterId, Integer  deliverableItemId, String  itemId) throws SQLException;*/
	 
	 //get rubric data rough 
	 @JdbcControl.SQL(statement =" select rubric_instruction ,itemsubtestid, itensetid  from table name dual")
	 String[] getRubricData(Integer itemSubtestId, Integer  itemSetId) throws SQLException;
	 
	 //get response for CR item
	 @JdbcControl.SQL(statement =" select constructed_response from  item_response_cr irscr where irscr.test_roster_id = {testRosterId} and irscr.item_set_id ={deliverableItemId} and irscr.item_id = {itemId}")
	 Clob getCRItemResponse(Integer testRosterId, Integer deliverableItemId, String itemId);
	 
	 // get TestAdminDetails
	 @JdbcControl.SQL(statement = "select  test_admin.TEST_ADMIN_ID as testAdminId,  test_admin.CUSTOMER_ID as customerId,  test_admin.TEST_ADMIN_NAME as testAdminName,  test_admin.PRODUCT_ID as productId,  test_admin.CREATOR_ORG_NODE_ID as creatorOrgNodeId,  node.org_node_name as creatorOrgNodeName,  test_admin.ACCESS_CODE as accessCode,  test_admin.LOCATION as location,  test_admin.LOGIN_START_DATE as loginStartDate,  test_admin.LOGIN_END_DATE as loginEndDate,  test_admin.DAILY_LOGIN_START_TIME as dailyLoginStartTime,  test_admin.DAILY_LOGIN_END_TIME as dailyLoginEndTime,  users.USER_NAME as createdBy,  test_admin.CREATED_DATE_TIME as createdDateTime,  test_admin.ACTIVATION_STATUS as activationStatus,  test_admin.ITEM_SET_ID as itemSetId,  test_admin.TEST_ADMIN_STATUS as testAdminStatus,  test_admin.SESSION_NUMBER as sessionNumber,  test_admin.TEST_ADMIN_TYPE as testAdminType,  test_admin.PREFERRED_FORM as preferredForm,  test_admin.FORM_ASSIGNMENT_METHOD as formAssignmentMethod,  test_admin.TIME_ZONE as timeZone,  test_admin.SHOW_STUDENT_FEEDBACK as showStudentFeedback,  test_admin.ENFORCE_TIME_LIMIT as enforceTimeLimit,  test_admin.ENFORCE_BREAK as enforceBreak,  test.item_Set_name as testName,  nvl(report.completed, 'F') as reportable,  ontc.override_form_assignment as overrideFormAssignmentMethod,  ontc.override_login_start_date as overrideLoginStartDate,  test_admin.test_catalog_id as testCatalogId,  test_admin.program_id as programId,  test_admin.random_distractor_status as isRandomize,  test_admin.test_session_data_exported as isTestSessionDataExported from  test_admin, users, item_set test,  org_node node,  org_node_test_catalog ontc,  (select  \t  \t\t  ros.test_admin_id,  \t\t\t  decode(count(distinct ros.test_roster_id), 0, 'F', 'T') as completed  \t  \tfrom  \t\t\t  test_roster ros,  \t\t\t  test_admin \t  \twhere  \t\t\t  ros.test_admin_id = test_admin.test_admin_id \t\t\t  and ros.test_completion_Status = 'CO' \t\t\t  and ros.validation_status = 'VA' \t\t\t  and test_admin.activation_status = 'AC'  and test_admin.test_admin_id = {testAdminId} \t\tgroup by  \t\t\t  ros.test_admin_id) report where  test_admin.activation_status = 'AC'  and ontc.item_set_id (+) = test_admin.item_set_id  and ontc.org_node_id (+) = test_admin.creator_org_node_id  and node.org_node_id = test_Admin.creator_org_node_id  and test.item_Set_id = test_admin.item_set_id \t\tand test_Admin.test_admin_id = report.test_admin_id (+)  and test_admin.created_by = users.user_id  and test_admin.test_admin_id = {testAdminId}")
	 TestSession getTestAdminDetails(Integer testAdminId) throws SQLException;
	 
	 //@JdbcControl.SQL(statement = "insert into item_response_points   (points,    item_response_points_seq_num,    datapoint_id,    condition_code_id,    created_by,    item_response_id) values   ({scorableItem.points}, 1,   {item_response_points.datapoint_id}),   {item_response_points.condition_code_id},   {item_response_points.created_by},   {item_response_points.item_response_id})")
	// TestSession saveScore(ScorableItem scorableItem, String item_response_points ) throws SQLException;
	 
	// get all response details for save
	 @JdbcControl.SQL(statement = "select itemId as itemId,  responseId as responseId, dp.datapoint_id as datapointId from (select itemId as itemId, responseId as responseId  from (select item_id as itemId, item_response.item_response_id as responseId  from item_response  where item_set_id = {itemSetIdTD} and test_roster_id = {testRosterId}   and item_id = {itemId} order by item_response.response_seq_num desc) item_res   where rownum = 1) irrs,  datapoint dp  where dp.item_id = irrs.itemId  ")
	 ResponsePoints[] getResponseForScore(String itemId, Integer itemSetIdTD, Integer testRosterId ) throws SQLException;
	
	// save or update all response 
	 @JdbcControl.SQL(statement = "MERGE INTO item_response_points responsePt USING (select {responsePoints.responseId}   as responseId,  {responsePoints.datapointId} as datapointId, {responsePoints.point}  as point, {responsePoints.createdBy}    as createdBy, {responsePoints.creattionDate}  as createdDate  from dual) temp ON (responsePt.item_response_id = temp.responseId and responsePt.datapoint_id = temp.datapointId) WHEN MATCHED THEN   UPDATE SET responsePt.points  = temp.point,  responsePt.created_by  = temp.createdBy,   responsePt.created_date_time = temp.createdDate WHEN NOT MATCHED THEN   INSERT  (responsePt.points,  responsePt.item_response_points_seq_num,      responsePt.datapoint_id,      responsePt.item_response_id,      responsePt.created_by,      responsePt.created_date_time)   VALUES     (temp.point,      1,      temp.datapointId,      temp.responseId,      temp.createdBy,      temp.createdDate)")
	 int  saveOrUpdateScore( ResponsePoints responsePoints  ) throws SQLException;
	 
	//Data details for rubric view
	 @JdbcControl.SQL(statement = "select rdata.item_id as itemId, rdata.score as score, rdata.rubric_description as rubricDescription, rdataEx.sample_response as sampleResponse, rdataEx.rubric_explanation as rubricExplanation from item_rubric_data rdata, item_rubric_exemplars rdataEx where rdata.item_id = rdataEx.item_id(+) and rdata.score = rdataEx.score(+) and rdata.item_id = {itemId} order by rdata.score asc")
	 RubricViewData[] getRubricDataDetails(String itemId) throws SQLException; 
	 
	  // Change for HandScoring: score by student
	 @JdbcControl.SQL(statement = "select decode(count(*), 0, 'CO', 'IN') recordno   from (select derivedResPoint.datapoint_id, derivedResPoint.item_response_id,  count(resPoint.datapoint_id) recordcount from (select distinct dp.datapoint_id     datapoint_id, ir.item_response_id item_response_id from item_response_cr irs,  student_item_set_status siss,   item_set_ancestor ita, item it, datapoint dp,   item_response ir,  (select max(response_seq_num) seq_response_id, item_set_id,  test_roster_id,  item_id  from item_response    where test_roster_id = {rosterId} group by item_set_id, test_roster_id, item_id) derivedrs   where siss.test_roster_id = {rosterId}   and ita.ancestor_item_set_id = {itemSetIDTC}   and ita.item_set_type = 'TD' and siss.test_roster_id = irs.test_roster_id  and ita.item_set_id = irs.item_set_id  and it.item_id = irs.item_id  and ((upper(it.item_type) = 'CR' and   (it.answer_area is null or  upper(it.answer_area) = upper('AudioItem')))) and dp.item_id = it.item_id  and (irs.constructed_response is not null and  (decode(it.answer_area,   null, decode(instr(constructed_response, 'CDATA'),    0,   0,   1),   1)) = 1)    and derivedrs.item_set_id = ita.item_set_id  and derivedrs.test_roster_id = siss.test_roster_id    and derivedrs.item_id = it.item_id   and ir.response_seq_num = derivedrs.seq_response_id    and ir.item_set_id = ita.item_set_id  and ir.test_roster_id = siss.test_roster_id   and ir.item_id = it.item_id) derivedResPoint,  item_response_points resPoint   where derivedResPoint.datapoint_id = resPoint.datapoint_id(+)    and derivedResPoint.item_response_id = resPoint.item_response_id(+)   group by derivedResPoint.datapoint_id,   derivedResPoint.item_response_id having count(resPoint.datapoint_id) = 0) derived")
     String getScoringStatus(Integer rosterId, Integer itemSetIDTC) throws SQLException;
	 
	 
}