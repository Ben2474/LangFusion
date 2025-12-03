package edu.farmingdale.langfusion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.farmingdale.langfusion.data.LangFusionDatabase
import edu.farmingdale.langfusion.data.User
import edu.farmingdale.langfusion.data.UserProgress
import edu.farmingdale.langfusion.data.UserProgressDao
import edu.farmingdale.langfusion.ui.theme.LangFusionTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var currentUserId: Long? = null
    private var currentQuizScore: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = LangFusionDatabase.getInstance(this)
        val userDao = db.userDao()
        val userProgressDao = db.userProgressDao()

        setContent {
            LangFusionTheme {

                val navController = rememberNavController()
                var loginError by remember {mutableStateOf<String?>(null) }

                NavHost(navController = navController, startDestination = "welcome"){
                    composable("welcome"){
                        WelcomeScreen(
                            onLoginClick = {navController.navigate("login")},
                            onRegisterClick = {navController.navigate("register")}
                        )
                    }

                    composable("login") {LoginScreen(onLoginClick = {email, password -> lifecycleScope.launch{
                        val user = userDao.getUserByEmail(email)
                        if (user != null && user.passwordHash == password) {
                            currentUserId = user.id
                            loginError = null
                            navController.navigate("home")
                        } else {
                            loginError = "Invalid email or password"
                        }
                    }
                    },
                        errorMessage = loginError,
                        onDismissError = {loginError = null}
                    )
                    }
                    composable("register") {RegisterScreen{ first, last, dob, email, password ->
                        lifecycleScope.launch {
                            val user = User(
                                firstName = first,
                                lastName = last,
                                dateOfBirth = dob,
                                email = email,
                                passwordHash = password
                            )
                            userDao.insertUser(user)
                            navController.navigate("login")
                        }
                    }
                    }
                    composable("home") {HomeScreen(
                        onQuickStartClick = {navController.navigate("lessonMenu")},
                        onQuizzesClick =  {navController.navigate("quizMenu")},
                        onChallengesClick = {navController.navigate("weeklyChallengeMenu")},
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                        )
                    }
                    composable("lessonMenu") {LessonMenuScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")},
                        onSpanishLessonMenuClick = {navController.navigate("spanishLessons")},
                        onFrenchLessonMenuClick = {navController.navigate("frenchLessons")},
                        onItalianLessonMenuClick = {navController.navigate("italianLessons")}


                    )}

                    composable("quizMenu") {QuizMenuScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")},
                        onStartQuizClick = {
                            currentQuizScore = 0
                            navController.navigate("quiz1")}
                    )}
                    composable("weeklyChallengeMenu") {WeeklyChallengeMenuScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")},
                        onWeeklyChallengeClick = {navController.navigate("weeklyChallenge")}
                    )}
                    composable("profile") {ProfileScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")},
                        onProgressDashboardClick = {navController.navigate("progressDashboard")}
                    )}
                    composable("speech") {SpeechScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("chatbox") {ChatBoxScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("settings") {SettingsScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("progressDashboard"){
                        val uid = currentUserId ?: return@composable
                        ProgressDashboardScreen(
                            userId = uid,
                            userProgressDao = userProgressDao,
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}

                    composable("weeklyChallenge") {WeeklyChallengeScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("spanishLessons") {SpanishLessonScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")},
                        onSpanishLessonOneClick = {navController.navigate("spanishLesson1")},
                        onSpanishLessonTwoClick = {navController.navigate("spanishLesson2")},
                        onSpanishLessonThreeClick = {navController.navigate("spanishLesson3")}
                        )}
                    composable("spanishLesson1") {SpanishLessonOneScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("spanishLesson2") {SpanishLessonTwoScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("spanishLesson3") {SpanishLessonThreeScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("frenchLessons") {FrenchLessonScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")},
                        onFrenchLessonOneClick = {navController.navigate("frenchLesson1")},
                        onFrenchLessonTwoClick = {navController.navigate("frenchLesson2")},
                        onFrenchLessonThreeClick = {navController.navigate("frenchLesson3")}
                    )}
                    composable("frenchLesson1") {FrenchLessonOneScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("frenchLesson2") {FrenchLessonTwoScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("frenchLesson3") {FrenchLessonThreeScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("italianLessons") {ItalianLessonScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")},
                        onItalianLessonOneClick = {navController.navigate("italianLesson1")},
                        onItalianLessonTwoClick = {navController.navigate("italianLesson2")},
                        onItalianLessonThreeClick = {navController.navigate("italianLesson3")}
                    )}
                    composable("italianLesson1") {ItalianLessonOneScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("italianLesson2") {ItalianLessonTwoScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("italianLesson3") {ItalianLessonThreeScreen(
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("quiz1") {QuizQOneScreen(
                        onNextQuestion = { navController.navigate("quiz2")},
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("quiz2") {QuizQTwoScreen(
                        onNextQuestion = { navController.navigate("quiz3")},
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("quiz3") {QuizQThreeScreen(
                        onNextQuestion = { navController.navigate("quiz4")},
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("quiz4") {QuizQFourScreen(
                        onNextQuestion = { navController.navigate("quiz5")},
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                    composable("quiz5") {QuizQFiveScreen(
                        totalScore = currentQuizScore,
                        onSubmitQuiz = {totalScore ->
                            val userId = currentUserId ?: return@QuizQFiveScreen

                            lifecycleScope.launch {
                                val progress = UserProgress(
                                    userId = userId,
                                    language = "Spanish",
                                    lessonId = "quiz",
                                    completed = true,
                                    lastScore = totalScore
                                )
                                userProgressDao.insertOrUpdateProgress(progress)
                            }
                            navController.navigate("home")
                        },
                        onProfileClick = {navController.navigate("profile")},
                        onHomeIconClick = {navController.navigate("home")},
                        onLessonIconClick = {navController.navigate("lessonMenu")},
                        onSpeechIconClick = {navController.navigate("speech")},
                        onChatboxIconClick = {navController.navigate("chatbox")},
                        onSettingsIconClick = {navController.navigate("settings")}
                    )}
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.welcome),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier
            .offset(y = 495.dp)
            .fillMaxWidth()
            .height(56.dp)
            .clickable {onLoginClick()}
        )
        Box(modifier = Modifier
            .offset(y = 600.dp)
            .fillMaxWidth()
            .height(56.dp)
            .clickable {onRegisterClick()}
        )
    }
}
@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    errorMessage: String? = null,
    onDismissError: () -> Unit = {}
){
    var email by remember {mutableStateOf("")}
    var password by remember {mutableStateOf("")}

    Box(Modifier.fillMaxSize()){
        Image(
            painter = painterResource(R.drawable.login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(290.dp))

            InvisibleField(
                value = email,
                onChange = { email = it },
                isPassword = false
            )

            Spacer(modifier = Modifier.height(100.dp))

            InvisibleField(
                value = password,
                onChange = { password = it },
                isPassword = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { onLoginClick(email, password) }
            )
        }
    }

    if (errorMessage != null){
        AlertDialog(
            onDismissRequest = onDismissError,
            confirmButton = {
                TextButton(onClick = onDismissError){
                    Text("Ok")
                }
            },
            title = { Text("Login error", style = MaterialTheme.typography.titleLarge)},
            text = {Text(errorMessage)}
        )
    }
}
@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String, String, String) -> Unit
){
    var firstName by remember {mutableStateOf("")  }
    var lastName by remember {mutableStateOf("")  }
    var dob by remember {mutableStateOf("")  }
    var email by remember {mutableStateOf("")  }
    var password by remember {mutableStateOf("")  }
    var errorMessage by remember {mutableStateOf<String?>(null)  }

    fun isValidEmail(text: String): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.register),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(220.dp))

            InvisibleField(firstName, onChange = {firstName = it })
            Spacer(modifier = Modifier.height(60.dp))

            InvisibleField(lastName, onChange =  { lastName = it })
            Spacer(modifier = Modifier.height(60.dp))

            InvisibleField(dob, onChange =  { dob = it })
            Spacer(modifier = Modifier.height(60.dp))

            InvisibleField(email, onChange =  { email = it })
            Spacer(modifier = Modifier.height(60.dp))

            InvisibleField(password, onChange =  { password = it }, isPassword = true)
            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable {
                        when {
                            firstName.isBlank() -> errorMessage = "First name is required"
                            lastName.isBlank() -> errorMessage = "Last name is required"
                            dob.isBlank() -> errorMessage = "Date of Birth is required"
                            email.isBlank() -> errorMessage = "Email is required"
                            !isValidEmail(email) -> errorMessage = "Enter a valid email"
                            password.length < 6 -> errorMessage = "Password should be at least 6 characters"
                            else -> {
                                errorMessage = null
                                onRegisterClick(firstName, lastName, dob, email, password)
                            }
                        }
                    }
            )
        }

        if (errorMessage != null){
            AlertDialog(
                onDismissRequest = {errorMessage = null},
                confirmButton = {
                    TextButton(onClick = {errorMessage = null}) {
                        Text("Ok")
                    }
                },
                title = {Text("Registration error")},
                text = {Text(errorMessage ?: "")}
            )
        }
    }
}

@Composable
fun InvisibleField(
    value: String,
    onChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = { },
        visualTransformation = if(isPassword){
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            cursorColor = Color.Black,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )
}

@Composable
fun HomeScreen(
    onQuickStartClick: () -> Unit = {},
    onQuizzesClick: () -> Unit = {},
    onChallengesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.home),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 500.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onQuickStartClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = (-230).dp, y = 620.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onQuizzesClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 620.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onChallengesClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}

@Composable
fun LessonMenuScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {},
    onSpanishLessonMenuClick: () -> Unit = {},
    onFrenchLessonMenuClick: () -> Unit = {},
    onItalianLessonMenuClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.lessonsmenu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 350.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onSpanishLessonMenuClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 450.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onFrenchLessonMenuClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 550.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onItalianLessonMenuClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun SpanishLessonScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {},
    onSpanishLessonOneClick: () -> Unit = {},
    onSpanishLessonTwoClick: () -> Unit = {},
    onSpanishLessonThreeClick: () -> Unit = {}
    ){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.spanishlessonmenu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 350.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onSpanishLessonOneClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 450.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onSpanishLessonTwoClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 550.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onSpanishLessonThreeClick()}
        )
    }
}
@Composable
fun ProgressDashboardScreen(
    userId: Long,
    userProgressDao: UserProgressDao,
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    var progressList by remember{mutableStateOf<List<UserProgress>>(emptyList())}
    LaunchedEffect(userId) {
        progressList = userProgressDao.getProgressForUser(userId)
    }

    val totalEntries = progressList.size
    val completedCount = progressList.count{it.completed}

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.progressdashboard),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 660.dp)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lessons/Quizzes Done: $completedCount",
                fontSize = 20.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Total Activities Tracked: $totalEntries",
                fontSize = 18.sp,
                color = Color.Black
            )
        }

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun QuizMenuScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {},
    onStartQuizClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.quizmenu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 400.dp)
                .fillMaxWidth()
                .height(60.dp)
                .clickable{onStartQuizClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun SettingsScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.settings),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun WeeklyChallengeMenuScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {},
    onWeeklyChallengeClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.weeklychallengemenu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 430.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onWeeklyChallengeClick()}
        )
    }
}
@Composable
fun WeeklyChallengeScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.weeklychallenge),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun ProfileScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {},
    onProgressDashboardClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.profile),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 550.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProgressDashboardClick()}
        )
    }
}
@Composable
fun SpeechScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.speech),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun ItalianLessonScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {},
    onItalianLessonOneClick: () -> Unit = {},
    onItalianLessonTwoClick: () -> Unit = {},
    onItalianLessonThreeClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.italianlessonmenu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 350.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onItalianLessonOneClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 450.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onItalianLessonTwoClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 550.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onItalianLessonThreeClick()}
        )
    }
}
@Composable
fun FrenchLessonScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {},
    onFrenchLessonOneClick: () -> Unit = {},
    onFrenchLessonTwoClick: () -> Unit = {},
    onFrenchLessonThreeClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.frenchlessonmenu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 350.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onFrenchLessonOneClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 450.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onFrenchLessonTwoClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 550.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onFrenchLessonThreeClick()}
        )
    }
}
@Composable
fun SpanishLessonOneScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.spanishlesson1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun SpanishLessonTwoScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.spanishlesson2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun SpanishLessonThreeScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.spanishlesson3),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun FrenchLessonOneScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.frenchlesson1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun FrenchLessonTwoScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.frenchlesson2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun FrenchLessonThreeScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.frenchlesson3),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun ItalianLessonOneScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.italianlesson1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun ItalianLessonTwoScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.italianlesson2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun ItalianLessonThreeScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.italianlesson3),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun QuizQOneScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {},
    onNextQuestion: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.quizq1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 600.dp)
                .fillMaxWidth()
                .height(80.dp)
                .clickable{onNextQuestion()}
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun QuizQTwoScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {},
    onNextQuestion: () -> Unit = {}


){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.quizq2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 600.dp)
                .fillMaxWidth()
                .height(80.dp)
                .clickable{onNextQuestion()}
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun QuizQThreeScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {},
    onNextQuestion: () -> Unit = {}


){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.quizq3),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 600.dp)
                .fillMaxWidth()
                .height(80.dp)
                .clickable{onNextQuestion()}
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun QuizQFourScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {},
    onNextQuestion: () -> Unit = {}


){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.quizq4),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 600.dp)
                .fillMaxWidth()
                .height(80.dp)
                .clickable{onNextQuestion()}
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun QuizQFiveScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {},
    totalScore: Int,
    onSubmitQuiz: (Int) -> Unit = {}

){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.quizq5),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 650.dp)
                .fillMaxWidth()
                .height(80.dp)
                .clickable{onSubmitQuiz(totalScore)}
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
@Composable
fun ChatBoxScreen(
    onProfileClick: () -> Unit = {},
    onHomeIconClick: () -> Unit = {},
    onLessonIconClick: () -> Unit = {},
    onSpeechIconClick: () -> Unit = {},
    onChatboxIconClick: () -> Unit = {},
    onSettingsIconClick: () -> Unit = {}
){
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.chatbox),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(x = 230.dp, y = 95.dp)
                .fillMaxWidth()
                .height(30.dp)
                .clickable{onProfileClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onHomeIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 105.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onLessonIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSpeechIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 270.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onChatboxIconClick()}
        )

        Box(
            modifier = Modifier
                .offset(x = 350.dp, y = 830.dp)
                .size(48.dp)
                .clickable{onSettingsIconClick()}
        )
    }
}
