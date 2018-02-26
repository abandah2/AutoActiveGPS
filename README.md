## AutoActiveGPS

Its an easy turn on GPS programatically and get currnt Location  . 

## How to integrate into your app?

Step 1. :

```java
public class MainActivity extends AppCompatActivity {

    ActiveGPS activeGPS=null;

```
Step 2. 
```java
 protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeGPS = new ActiveGPS(MainActivity.this);
        .
        .
```

Step 3. 
```java
activeGPS.TurnOnGPS( new ActiveGPS.GPSListener() {
                    @Override
                    public void GPS_IS_ON(Location location) {
                    }
                    
                    @Override
                    public void Permission_Grand_Cant_Turn_GPS_ON() {
                    }
                    
                    @Override
                    public void Permission_Denied() {
                    }
                    
                    @Override
                    public void OnError(String error) {
                    }
                    
                },true);
                   

        });
```

Step 4. 
```java
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            activeGPS.onRequestPermissionsResult(this, requestCode, permissions, grantResults);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
            activeGPS.onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            activeGPS.onActivityResult(this, requestCode, resultCode, data);

    }
```
