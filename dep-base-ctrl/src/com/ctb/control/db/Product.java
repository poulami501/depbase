package com.ctb.control.db; 

import com.bea.control.*;
import org.apache.beehive.controls.system.jdbc.JdbcControl;

import com.ctb.bean.testAdmin.TestProduct; 
import java.sql.SQLException; 
import org.apache.beehive.controls.api.bean.ControlExtension;

/** 
 * Defines a new database control. 
 * 
 * The @jc:connection tag indicates which WebLogic data source will be used by 
 * this database control. Please change this to suit your needs. You can see a 
 * list of available data sources by going to the WebLogic console in a browser 
 * (typically http://localhost:7001/console) and clicking Services, JDBC, 
 * Data Sources. 
 * 
 * @jc:connection data-source-jndi-name="oasDataSource" 
 */ 
@ControlExtension()
@JdbcControl.ConnectionDataSource(jndiName = "oasDataSource")
public interface Product extends JdbcControl
{ 
    /**
     * @jc:sql statement::
     * select 
     * 	   decode(count(ontc.product_id), 0, 'false', 'true') as visible
     * from
     * 	org_node_test_catalog ontc,
     * 	org_node_ancestor ona,
     * 	user_role urole,
     * 	users
     * where
     * 	 users.user_name = {userName}
     * 	 and urole.user_id = users.user_id
     * 	 and ona.ancestor_org_node_id = urole.org_node_id
     * 	 and ontc.org_node_id = ona.org_node_id
     * 	 and ontc.product_id = {productId}::
     */
    @JdbcControl.SQL(statement = "select  \t  decode(count(ontc.product_id), 0, 'false', 'true') as visible from \torg_node_test_catalog ontc, \torg_node_ancestor ona, \tuser_role urole, \tusers where \t users.user_name = {userName} \t and urole.user_id = users.user_id \t and ona.ancestor_org_node_id = urole.org_node_id \t and ontc.org_node_id = ona.org_node_id \t and ontc.product_id = {productId}")
    String checkVisibility(String userName, Integer productId) throws SQLException;
    
    /**
     * @jc:sql statement::
     * select distinct
     *      prod.product_id as productId,
     *      prod.product_name as productName,
     *      prod.version as version,
     *      prod.product_description as productDescription,
     *      prod.created_by as createdBy,
     *      prod.created_date_time as createdDateTime,
     *      prod.updated_by as updatedBy,
     *      prod.updated_date_time as updatedDateTime,
     *      prod.activation_status as activationStatus,
     *      prod.product_type as productType,
     *      prod.scoring_item_set_level as scoringItemSetLevel,
     *      prod.preview_item_set_level as previewItemSetLevel,
     *      prod.parent_product_id as parentProductId,
     *      prod.ext_product_id as extProductId,
     *      prod.content_area_level as contentAreaLevel,
     *      prod.internal_display_name as internalDisplayName,
     *      prod.sec_scoring_item_set_level as secScoringItemSetLevel,
     *      prod.ibs_show_cms_id as ibsShowCmsId,
     *      prod.printable as printable,
     *      prod.scannable as scannable,
     *      prod.keyenterable as keyenterable,
     *      prod.branding_type_code as brandingTypeCode,
     *      prod.acknowledgments_url as acknowledgmentsURL,
     *      prod.show_student_feedback as showStudentFeedback,
     *      prod.static_manifest as staticManifest,
     *      prod.session_manifest as sessionManifest,
     *      prod.subtests_selectable as subtestsSelectable,
     *      prod.subtests_orderable as subtestsOrderable,
     *      prod.subtests_levels_vary as subtestsLevelsVary,
     *      prod.off_grade_testing_disabled as offGradeTestingDisabled,
	 *		prod.license_enabled as productLicenseEnabled
     * from
     *      product prod,
     *      test_catalog cat,
     *      org_node_test_catalog ontc,
     *      user_role urole,
     *      users
     * where
     *      prod.activation_status = 'AC'
     *      and prod.product_id = cat.product_id
     *      and cat.activation_status = 'AC'
     *      and cat.test_catalog_id = ontc.test_catalog_id
     *      and ontc.activation_status = 'AC'
     *      and urole.org_node_id = ontc.org_node_id
     *      and urole.activation_status = 'AC'
     *      and users.user_id = urole.user_id
     *      and users.user_name = {userName}::
     *      array-max-length="all"
     */
    @JdbcControl.SQL(statement = "select distinct  prod.product_id as productId,  prod.product_name as productName,  prod.version as version,  prod.product_description as productDescription,  prod.created_by as createdBy,  prod.created_date_time as createdDateTime,  prod.updated_by as updatedBy,  prod.updated_date_time as updatedDateTime,  prod.activation_status as activationStatus,  prod.product_type as productType,  prod.scoring_item_set_level as scoringItemSetLevel,  prod.preview_item_set_level as previewItemSetLevel,  prod.parent_product_id as parentProductId,  prod.ext_product_id as extProductId,  prod.content_area_level as contentAreaLevel,  prod.internal_display_name as internalDisplayName,  prod.sec_scoring_item_set_level as secScoringItemSetLevel,  prod.ibs_show_cms_id as ibsShowCmsId,  prod.printable as printable,  prod.scannable as scannable,  prod.keyenterable as keyenterable,  prod.branding_type_code as brandingTypeCode,  prod.acknowledgments_url as acknowledgmentsURL,  prod.show_student_feedback as showStudentFeedback,  prod.static_manifest as staticManifest,  prod.session_manifest as sessionManifest,  prod.subtests_selectable as subtestsSelectable,  prod.subtests_orderable as subtestsOrderable,  prod.subtests_levels_vary as subtestsLevelsVary,  prod.off_grade_testing_disabled as offGradeTestingDisabled, \t\tprod.license_enabled as productLicenseEnabled from  product prod,  test_catalog cat,  org_node_test_catalog ontc,  user_role urole,  users where  prod.activation_status = 'AC'  and prod.product_id = cat.product_id  and cat.activation_status = 'AC'  and cat.test_catalog_id = ontc.test_catalog_id  and ontc.activation_status = 'AC'  and urole.org_node_id = ontc.org_node_id  and urole.activation_status = 'AC'  and users.user_id = urole.user_id  and users.user_name = {userName}",
                     arrayMaxLength = 100000)
    TestProduct [] getProductsForUser(String userName) throws SQLException;


    /**
     * @jc:sql statement::
     * select distinct
     *      prod.product_id as productId,
     *      prod.product_name as productName,
     *      prod.version as version,
     *      prod.product_description as productDescription,
     *      prod.created_by as createdBy,
     *      prod.created_date_time as createdDateTime,
     *      prod.updated_by as updatedBy,
     *      prod.updated_date_time as updatedDateTime,
     *      prod.activation_status as activationStatus,
     *      prod.product_type as productType,
     *      prod.scoring_item_set_level as scoringItemSetLevel,
     *      prod.preview_item_set_level as previewItemSetLevel,
     *      prod.parent_product_id as parentProductId,
     *      prod.ext_product_id as extProductId,
     *      prod.content_area_level as contentAreaLevel,
     *      prod.internal_display_name as internalDisplayName,
     *      prod.sec_scoring_item_set_level as secScoringItemSetLevel,
     *      prod.ibs_show_cms_id as ibsShowCmsId,
     *      prod.printable as printable,
     *      prod.scannable as scannable,
     *      prod.keyenterable as keyenterable,
     *      prod.branding_type_code as brandingTypeCode,
     *      prod.acknowledgments_url as acknowledgmentsURL,
     *      prod.show_student_feedback as showStudentFeedback,
     *      prod.static_manifest as staticManifest,
     *      prod.session_manifest as sessionManifest,
     *      prod.subtests_selectable as subtestsSelectable,
     *      prod.subtests_orderable as subtestsOrderable,
     *      prod.subtests_levels_vary as subtestsLevelsVary,
     *      cec.support_phone_number as supportPhoneNumber,
     *      prod.off_grade_testing_disabled as offGradeTestingDisabled
     * from
     *      product prod, test_admin adm, customer_email_config cec
     * where
     *      prod.product_id = adm.product_id
     *      and cec.customer_id (+) = adm.customer_id 
     *      and adm.test_admin_id = {testAdminId}::
     *      array-max-length="all"
     */
    @JdbcControl.SQL(statement = "select distinct  prod.product_id as productId,  prod.product_name as productName,  prod.version as version,  prod.product_description as productDescription,  prod.created_by as createdBy,  prod.created_date_time as createdDateTime,  prod.updated_by as updatedBy,  prod.updated_date_time as updatedDateTime,  prod.activation_status as activationStatus,  prod.product_type as productType,  prod.scoring_item_set_level as scoringItemSetLevel,  prod.preview_item_set_level as previewItemSetLevel,  prod.parent_product_id as parentProductId,  prod.ext_product_id as extProductId,  prod.content_area_level as contentAreaLevel,  prod.internal_display_name as internalDisplayName,  prod.sec_scoring_item_set_level as secScoringItemSetLevel,  prod.ibs_show_cms_id as ibsShowCmsId,  prod.printable as printable,  prod.scannable as scannable,  prod.keyenterable as keyenterable,  prod.branding_type_code as brandingTypeCode,  prod.acknowledgments_url as acknowledgmentsURL,  prod.show_student_feedback as showStudentFeedback,  prod.static_manifest as staticManifest,  prod.session_manifest as sessionManifest,  prod.subtests_selectable as subtestsSelectable,  prod.subtests_orderable as subtestsOrderable,  prod.subtests_levels_vary as subtestsLevelsVary,  cec.support_phone_number as supportPhoneNumber,  prod.off_grade_testing_disabled as offGradeTestingDisabled from  product prod, test_admin adm, customer_email_config cec where  prod.product_id = adm.product_id  and cec.customer_id (+) = adm.customer_id  and adm.test_admin_id = {testAdminId}",
                     arrayMaxLength = 100000)
    TestProduct getProductForTestAdmin(Integer testAdminId) throws SQLException;

    /**
     * @jc:sql statement::
     * select distinct
     *      prod.product_id as productId,
     *      prod.product_name as productName,
     *      prod.version as version,
     *      prod.product_description as productDescription,
     *      prod.created_by as createdBy,
     *      prod.created_date_time as createdDateTime,
     *      prod.updated_by as updatedBy,
     *      prod.updated_date_time as updatedDateTime,
     *      prod.activation_status as activationStatus,
     *      prod.product_type as productType,
     *      prod.scoring_item_set_level as scoringItemSetLevel,
     *      prod.preview_item_set_level as previewItemSetLevel,
     *      prod.parent_product_id as parentProductId,
     *      prod.ext_product_id as extProductId,
     *      prod.content_area_level as contentAreaLevel,
     *      prod.internal_display_name as internalDisplayName,
     *      prod.sec_scoring_item_set_level as secScoringItemSetLevel,
     *      prod.ibs_show_cms_id as ibsShowCmsId,
     *      prod.printable as printable,
     *      prod.scannable as scannable,
     *      prod.keyenterable as keyenterable,
     *      prod.branding_type_code as brandingTypeCode,
     *      prod.acknowledgments_url as acknowledgmentsURL,
     *      prod.show_student_feedback as showStudentFeedback,
     *      prod.static_manifest as staticManifest,
     *      prod.session_manifest as sessionManifest,
     *      prod.subtests_selectable as subtestsSelectable,
     *      prod.subtests_orderable as subtestsOrderable,
     *      prod.subtests_levels_vary as subtestsLevelsVary,
     *      prod.off_grade_testing_disabled as offGradeTestingDisabled
     * from
     *      product prod
     * where
     *      prod.product_id  = {testProductId}::
     *      array-max-length="all"
     */
    @JdbcControl.SQL(statement = "select distinct  prod.product_id as productId,  prod.product_name as productName,  prod.version as version,  prod.product_description as productDescription,  prod.created_by as createdBy,  prod.created_date_time as createdDateTime,  prod.updated_by as updatedBy,  prod.updated_date_time as updatedDateTime,  prod.activation_status as activationStatus,  prod.product_type as productType,  prod.scoring_item_set_level as scoringItemSetLevel,  prod.preview_item_set_level as previewItemSetLevel,  prod.parent_product_id as parentProductId,  prod.ext_product_id as extProductId,  prod.content_area_level as contentAreaLevel,  prod.internal_display_name as internalDisplayName,  prod.sec_scoring_item_set_level as secScoringItemSetLevel,  prod.ibs_show_cms_id as ibsShowCmsId,  prod.printable as printable,  prod.scannable as scannable,  prod.keyenterable as keyenterable,  prod.branding_type_code as brandingTypeCode,  prod.acknowledgments_url as acknowledgmentsURL,  prod.show_student_feedback as showStudentFeedback,  prod.static_manifest as staticManifest,  prod.session_manifest as sessionManifest,  prod.subtests_selectable as subtestsSelectable,  prod.subtests_orderable as subtestsOrderable,  prod.subtests_levels_vary as subtestsLevelsVary,  prod.off_grade_testing_disabled as offGradeTestingDisabled from  product prod where  prod.product_id  = {testProductId}", arrayMaxLength = 100000)
    TestProduct getProduct(Integer testProductId) throws SQLException;


    static final long serialVersionUID = 1L;
}