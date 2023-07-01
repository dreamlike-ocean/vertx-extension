package top.dreamlike

import org.apache.ibatis.annotations.Select
import top.dreamlike.db.MybatisMarkInterface


data class Owner(
    val id: Long?,
    val firstName: String,
    val secondName: String,
    val address: String,
    val city: String,
    val telephone: String
) {


    interface OwnerMapper : MybatisMarkInterface {
        @Select("select * from owners")
        fun findAll(): List<Owner>
    }

    override fun toString(): String {
        return "Owner{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", telephone='" + telephone + '\'' +
                '}'
    }
}
