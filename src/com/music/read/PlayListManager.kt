package com.music.read

import com.google.gson.reflect.TypeToken

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.reflect.Type
import java.util.ArrayList

/**
 * Created by xupanpan on 06/11/2017.
 */
object PlayListManager {

 val PLAY_LIST_FILE = "play_list.json"
 val CONFIG_FILE = "config.json"



 val historyPlayList:List<MP3Info>?
get() =getLocalArray<List<MP3Info>>(PLAY_LIST_FILE, object:TypeToken<ArrayList<MP3Info>>() {

}.type)


 val playConfig:Config?
get() =getLocalObject<Config>(CONFIG_FILE, Config::class.java)


 fun savePlayList(list:List<MP3Info>) {
save2Local(list, PLAY_LIST_FILE)
}


 fun savePlayConfig(config:Config) {
save2Local(config, CONFIG_FILE)
}




 fun save2Local(obj:Any?, fileName:String) {
if (obj != null)
{
try
{
val f = File(FileLoadUtils.localPath, fileName)
val fos = FileOutputStream(f)
fos.write(GsonUtils.gson.toJson(obj).toByteArray(charset("UTF-8")))
fos.flush()
fos.close()
}
catch (e:Exception) {
e.printStackTrace()
}

}
}


 fun <T> getLocalObject(fileName:String, tClass:Class<T>):T? {

val file = File(FileLoadUtils.localPath, fileName)
if (!file.exists())
{
return null
}
try
{
val s = FileLoadUtils.readTextFile(FileInputStream(file))
if (s != null && s != "")
{
val t = GsonUtils.gson.fromJson<T>(s, tClass)
return t
}

}
catch (e:Exception) {

}

return null
}


 fun <T> getLocalArray(fileName:String, type:Type):T? {

val file = File(FileLoadUtils.localPath, fileName)
if (!file.exists())
{
return null
}
try
{
val s = FileLoadUtils.readTextFile(FileInputStream(file))
if (s != null && s != "")
{
val t = GsonUtils.gson.fromJson<T>(s, type)
return t
}

}
catch (e:Exception) {

}

return null
}


}
