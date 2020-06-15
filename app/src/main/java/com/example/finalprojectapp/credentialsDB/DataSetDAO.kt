package com.example.finalprojectapp.credentialsDB

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView

import com.example.finalprojectapp.data.model.relationship.DataSetCredentialsManyToMany
import com.example.finalprojectapp.data.model.relationship.DataSetWithCredentials
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel

@Dao
interface DataSetDAO {


    @Query("SELECT * FROM dataSet_ Where :hashData = hashData")
    suspend fun privateGetDataSetByHash(hashData: String): DataSet

    @Update
    fun privateUpdateNewCre(vararg newManyToMany: DataSetCredentialsManyToMany): Int

    @Query("SELECT r.DataSetCredentialsManyToManyID FROM  dataSetCredentialsManyToMany r , credentials_ c   Where :dataSetID = r.dataSetId AND r.credentialsId = c.credentialsId And c.hint Like :hints")
    suspend fun privateGetUnionDataSetAndCredentialsHash(dataSetID: Long, hints: String): Long?


    @Query("Delete from dataSetCredentialsManyToMany")
    suspend fun deleteAllR()

    @Query("Delete from dataSet_")
    suspend fun deleteAllDataSets()

    @Query("DELETE FROM dataSetCredentialsManyToMany  WHERE dataSetId=:dataSetId")
    suspend fun deleteFromRelationship(dataSetId: Long): Int

    @Query("Select d.dataSetName,d.dataSetId from dataSet_ d where d.serviceId=:serviceId ")
    fun publicGetAllDataSetsByServiceId(serviceId: Long): LiveData<List<LayoutDataSetView>>

    @Query("Select c.iv,c.data,c.hint,c.credentialsId from dataSetCredentialsManyToMany r,credentials_ c where r.dataSetId=:dataSetId and r.credentialsId = c.credentialsId")
    fun publicGetAllCredentialsByDataSetID(dataSetId: Long): LiveData<List<LayoutCredentialView>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertDataSet(dataSet: DataSet): Long

    @Query("SELECT * FROM dataSet_ Where :hashData = dataSetId")
    suspend fun privateGetDataSetByDataSetID(hashData: Long): DataSet

    @Query("SELECT * FROM dataSetCredentialsManyToMany Where dataSetId =:num ")
    suspend fun privateGetDataSetToCredentials(num: Long): List<DataSetCredentialsManyToMany>


    @Transaction
    @Query("SELECT * FROM dataSet_")
    fun getUsersWithPlaylists(): List<DataSetWithCredentials>


    // TODO: 15/06/2020 experimental new way for data set query
    @Query("SELECT d.dataSetId as dataSetId ," +
            " d.serviceId as serviceId" +
            ",d.dataSetName as dataSetName," +
            "d.hashData as dataSetHash ," +
            "c.credentialsId as credentialID," +
            "c.hint as credentialHints," +
            "c.data as credentialData," +
            "c.encryptPasswordHash as credentialEncryptPasswordHash," +
            " c.encryptType as  credentialEncryptType," +
            " c.innerHashValue as credentialHash," +
            " c.iv as credentialIV," +
            "c.salt as CredentialSalt" +
            " FROM dataSetCredentialsManyToMany r,dataSet_ d ,credentials_ c Where d.dataSetId = :num and r.dataSetId = :num and r.credentialsId = c.credentialsId")
    suspend fun privateGetDataSetToCredentials2(num: Long): List<Data>

    data class Data(val dataSetId: Long,val serviceId: Long?,val dataSetName:String,val dataSetHash:String
    ,val credentialID: Long,val credentialData:String,val credentialHints:List<String>,val credentialEncryptPasswordHash:String?,
    val credentialEncryptType:String,val credentialHash:String,val credentialIV:String?,val CredentialSalt:String?)

    @Query("SELECT * FROM dataSetCredentialsManyToMany  Where :dataSetId like dataSetId")
    fun findAllRelationshipToDataSet(vararg dataSetId: Long): List<DataSetCredentialsManyToMany>

    @Delete
    fun deleteDataSetRelationship(vararg dataSetCredentialsManyToMany: DataSetCredentialsManyToMany)

    @Delete
    suspend fun deleteDataSet(vararg dataSet: DataSet)

    @Query("SELECT * FROM dataSet_  Where :hashData like hashData")
    suspend fun privateFindByHashData(hashData: String): DataSet

    @Query("SELECT * FROM dataSet_  Where :hashData like hashData And serviceId = :service")
    suspend fun privateFindByHashDataAndServiceId(hashData: String, service: Long): DataSet?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertCredentials(dataSetCredentialsManyToMany: DataSetCredentialsManyToMany): Long

    @Query("DELETE FROM dataSetCredentialsManyToMany  WHERE credentialsId=:credentialId and dataSetId=:dataSetId")
    fun deleteFromRelationshipCredential(credentialId: Long, dataSetId: Long): Int

    @Transaction
    @Query("select  r.credentialsId,c.innerHashValue hash, r.dataSetId dataSet from  credentials_ c,dataSetCredentialsManyToMany r where r.credentialsId=c.credentialsId and c.hint like '%Password%' or '%password%'   ")
    fun publicGetAllHashCredentials(): LiveData<List<DashboardViewModel.HashAndId>>

    @Query("SELECT * FROM dataSetCredentialsManyToMany  Where credentialsId=:credentialID")
    fun privateGetRelationshipCredential(credentialID: Long): List<DataSetCredentialsManyToMany>

    @Query("SELECT * FROM dataSet_")
    fun publicGetAllDataSet(): List<DataSet>

}