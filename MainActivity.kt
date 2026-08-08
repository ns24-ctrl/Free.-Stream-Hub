package com.freestreamhub.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import java.net.URLEncoder

data class Service(val name:String,val url:String,val note:String,val icon:String)
data class Category(val title:String,val query:String,val emoji:String)

val services=listOf(
 Service("YouTube","https://www.youtube.com","Free & official uploads","▶"),
 Service("JioHotstar","https://www.hotstar.com/in/","Official catalog","J"),
 Service("Sony LIV","https://www.sonyliv.com/","Free + premium","S"),
 Service("ZEE5","https://www.zee5.com/","Free + premium","Z"),
 Service("MX Player","https://www.mxplayer.in/","Ad-supported catalog","M")
)
val categories=listOf(
 Category("Free Movies","free full movie official","🎬"),
 Category("Web Series","free web series official","📺"),
 Category("Documentaries","free documentaries official","🌍"),
 Category("Music","official music videos","🎵"),
 Category("Kids","free kids cartoons official","🧸")
)

class MainActivity:ComponentActivity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContent{App()}}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun App(){
 var tab by remember{mutableIntStateOf(0)}
 var query by remember{mutableStateOf("")}
 var dark by remember{mutableStateOf(false)}
 val context=LocalContext.current
 val colors=if(dark) darkColorScheme() else lightColorScheme()
 MaterialTheme(colorScheme=colors){
  Scaffold(
   topBar={TopAppBar(
    title={Text("FreeStream Hub",fontWeight=FontWeight.Bold)},
    actions={IconButton({dark=!dark}){Text(if(dark)"☀" else "☾")}}
   )},
   bottomBar={NavigationBar{
    NavigationBarItem(tab==0,{tab=0},{Text("⌂")},{Text("Home")})
    NavigationBarItem(tab==1,{tab=1},{Text("⌕")},{Text("Search")})
    NavigationBarItem(tab==2,{tab=2},{Text("♡")},{Text("Saved")})
   }}
  ){pad->
   when(tab){
    0->Home(Modifier.padding(pad)){open(context,it)}
    1->Search(Modifier.padding(pad),query,{query=it}){q->
      val e=URLEncoder.encode(q,"UTF-8");open(context,"https://www.youtube.com/results?search_query=$e")
    }
    else->Saved(Modifier.padding(pad))
   }
  }
 }
}

fun open(c:android.content.Context,url:String){
 try{c.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(url)))}catch(_:Exception){}
}

@Composable fun Home(mod:Modifier,open:(String)->Unit){
 LazyColumn(mod.fillMaxSize().padding(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  item{
   Spacer(Modifier.height(6.dp))
   Text("Entertainment, without the hassle",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
   Text("Discover free and official content, then watch it on the service that hosts it.",modifier=Modifier.padding(top=4.dp))
  }
  item{Text("Browse categories",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=8.dp))}
  items(categories){cat->
   Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(18.dp)){
    Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){
     Text(cat.emoji,style=MaterialTheme.typography.headlineMedium)
     Column(Modifier.weight(1f).padding(start=14.dp)){
      Text(cat.title,fontWeight=FontWeight.Bold)
      Text("Find official/free results",style=MaterialTheme.typography.bodySmall)
     }
     Button({val e=URLEncoder.encode(cat.query,"UTF-8");open("https://www.youtube.com/results?search_query=$e")}){Text("Explore")}
    }
   }
  }
  item{Text("Services",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=8.dp))}
  items(services){s->
   OutlinedCard(Modifier.fillMaxWidth(),shape=RoundedCornerShape(16.dp)){
    Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){
     Surface(shape=RoundedCornerShape(12.dp),tonalElevation=3.dp){
      Box(Modifier.size(48.dp),contentAlignment=Alignment.Center){Text(s.icon,fontWeight=FontWeight.Bold)}
     }
     Column(Modifier.weight(1f).padding(horizontal=12.dp)){
      Text(s.name,fontWeight=FontWeight.Bold)
      Text(s.note,maxLines=1,overflow=TextOverflow.Ellipsis,style=MaterialTheme.typography.bodySmall)
     }
     TextButton({open(s.url)}){Text("Open")}
    }
   }
  }
  item{
   Spacer(Modifier.height(8.dp))
   Text("FreeStream Hub never bypasses subscriptions, DRM or geo-restrictions.",style=MaterialTheme.typography.bodySmall)
   Spacer(Modifier.height(12.dp))
  }
 }
}

@Composable fun Search(mod:Modifier,value:String,set:(String)->Unit,go:(String)->Unit){
 Column(mod.fillMaxSize().padding(16.dp)){
  Text("Search",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
  Text("Search YouTube for official/free content.",modifier=Modifier.padding(top=4.dp))
  OutlinedTextField(value,set,Modifier.fillMaxWidth().padding(top=16.dp),label={Text("What do you want to watch?")},singleLine=true)
  Button({if(value.isNotBlank())go(value)},Modifier.fillMaxWidth().padding(top=12.dp)){Text("Search")}
  Text("Try: “free full movie official”, “free web series official”",style=MaterialTheme.typography.bodySmall,modifier=Modifier.padding(top=12.dp))
 }
}

@Composable fun Saved(mod:Modifier){
 Column(mod.fillMaxSize().padding(20.dp)){
  Text("Saved",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
  Spacer(Modifier.height(8.dp))
  Text("Your watchlist is ready for a future update with local saving.")
  Spacer(Modifier.height(16.dp))
  Card(Modifier.fillMaxWidth()){
   Column(Modifier.padding(16.dp)){
    Text("Coming next",fontWeight=FontWeight.Bold)
    Text("Persistent watchlist, richer catalogs, thumbnails and deep links to installed official apps.")
   }
  }
 }
}
