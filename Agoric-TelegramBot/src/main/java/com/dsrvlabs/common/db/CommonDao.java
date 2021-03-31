package com.dsrvlabs.common.db;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class CommonDao {
	
    private Gson gson = new Gson();
    static Logger logger = Logger.getLogger(CommonDao.class.getName()); // Log4J
    
    public static void main(String[] args) {
    	CommonDao dao = new CommonDao();
    	
    	String query = "DbMapper.TEST23";
    	HashMap p = new HashMap();
    	p.put("resultSeq", "19325");
    	
    	
    	HashMap<String, Object> result = dao.commonSelectOne(query, p);
    	System.out.println(result);
    }
	
	public HashMap<String, Object> commonSelectOne(String query, HashMap p) {
		
		HashMap result = null;
		SqlSession session = MyBatisManager.getSqlMapper().openSession();
        try {
        	result = (HashMap<String, Object>)session.selectOne(query, p);
        } catch (Exception e){
        	logger.error(query, e);
        } finally {
        	session.close();
        }
        return result;
	}
	
	public HashMap<String, Object> commonSelectOneObject(String query, HashMap p) {
		
		HashMap result = null;
		SqlSession session = MyBatisManager.getSqlMapper().openSession();
        try {
        	result = (HashMap<String, Object>)session.selectOne(query, p);
        } catch (Exception e){
        	logger.error(query, e);
        } finally {
        	session.close();
        }
        return result;
	}
	
	public ArrayList<HashMap> commonSelectList(String query, HashMap p) {
        ArrayList<HashMap> list = new ArrayList();
        SqlSession session = MyBatisManager.getSqlMapper().openSession();
        try {
        	list = (ArrayList<HashMap>) session.selectList(query, p);
        } catch (Exception e){
        	logger.error(query, e);
        } finally {
        	session.close();
        }
        return list;
	}
	
	public int commonInsert(String query, HashMap p) {
		
        int result = 0;
        SqlSession session = MyBatisManager.getSqlMapper().openSession();
        try {
        	result = session.insert(query, p);
        	// result == 2 in the case of *ON DUPLICATE KEY UPDATE* 
        	if (result == 1 || result == 2) {
        		session.commit();
        	} else {
        		session.rollback();
        	}
        } catch (Exception e){
        	logger.error(query, e);
        } finally {
        	session.close();
        }
        return result;
	}
	
	
	public int commonUpdate( String query, HashMap p){
		
        int result = 0;
        SqlSession session = MyBatisManager.getSqlMapper().openSession();
        try {
        	result = session.update(query, p);
        	if (result == 1) { 
        		session.commit();
        	} else {
        		session.rollback();
        		System.out.println("##### update ROLLBACK !!!! : " + result);
        	}
        } catch (Exception e){
        	logger.error(query, e);
        } finally {
        	session.close();
        }
        return result;
	}
	
	public int commonDelete( String query, HashMap p){
		
		System.out.println(" ###### CommonDao.commonDelete ###### ");
		
        int count = 0;
        SqlSession session = MyBatisManager.getSqlMapper().openSession();
        try {
        	count = session.delete(query, p);
        	session.commit();
        } catch (Exception e){
        	logger.error(query, e);
        } finally {
        	session.close();
        }
        
        return count;
	}
}
