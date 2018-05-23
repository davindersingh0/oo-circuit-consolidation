-- Added function to delete CM_CI Attributes to remove Attributes belonging to old circuit CI MD Class 
-- deletes CmsCIAttribute 
CREATE OR REPLACE FUNCTION cm_delete_ci_attribute(p_ci_attr_id bigint, p_df_value text, p_dj_value text, p_owner character varying, p_comments character varying)
  RETURNS void 
  AS
$BODY$
DECLARE
    l_ci_id bigint;
    l_attribute_id integer;
    l_attribute_name character varying;
	l_dj_attribute_value text;
	l_df_attribute_value text;
BEGIN
    select into l_dj_attribute_value, l_df_attribute_value, l_attribute_id, l_attribute_name   
		 a.dj_attribute_value, a.df_attribute_value, a.attribute_id, cl.attribute_name   
    from cm_ci_attributes a, md_class_attributes cl
    where a.ci_attribute_id = p_ci_attr_id 
      and a.attribute_id = cl.attribute_id;

    delete from cm_ci_attributes 
    where ci_attribute_id = p_ci_attr_id
    returning ci_id into l_ci_id;

    update cm_ci
    set updated = now()
    where ci_id = l_ci_id;

    insert into cm_ci_attribute_log(log_id, log_time, log_event, ci_id, ci_attribute_id, attribute_id, attribute_name, comments, owner, dj_attribute_value, dj_attribute_value_old, df_attribute_value, df_attribute_value_old) 
    values (nextval('log_pk_seq'), now(), 300, l_ci_id, p_ci_attr_id, l_attribute_id, l_attribute_name, p_comments, p_owner, coalesce(p_dj_value, l_dj_attribute_value), l_dj_attribute_value, coalesce(p_df_value, l_df_attribute_value), l_df_attribute_value);

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;