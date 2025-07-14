package com.bestamibakir.rentagri.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bestamibakir.rentagri.ui.screens.auth.LoginScreen
import com.bestamibakir.rentagri.ui.screens.auth.RegisterScreen
import com.bestamibakir.rentagri.ui.screens.financial.FinancialScreen
import com.bestamibakir.rentagri.ui.screens.home.HomeScreen
import com.bestamibakir.rentagri.ui.screens.listings.CreateListingScreen
import com.bestamibakir.rentagri.ui.screens.listings.ListingDetailScreen
import com.bestamibakir.rentagri.ui.screens.listings.ListingsScreen
import com.bestamibakir.rentagri.ui.screens.market.MarketScreen
import com.bestamibakir.rentagri.ui.screens.profile.EditProfileScreen
import com.bestamibakir.rentagri.ui.screens.profile.ProfileScreen
import com.bestamibakir.rentagri.ui.screens.reports.ReportsScreen
import com.bestamibakir.rentagri.ui.screens.splash.SplashScreen

object NavDestinations {
    const val SPLASH_ROUTE = "splash"
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val HOME_ROUTE = "home"
    const val LISTINGS_ROUTE = "listings"
    const val LISTING_DETAIL_ROUTE = "listing_detail"
    const val CREATE_LISTING_ROUTE = "create_listing"
    const val PROFILE_ROUTE = "profile"
    const val EDIT_PROFILE_ROUTE = "edit_profile"
    const val FINANCIAL_ROUTE = "financial"
    const val MARKET_ROUTE = "market"
    const val REPORTS_ROUTE = "reports"


    const val LISTING_ID_ARG = "listingId"
}

@Composable
fun RentAgriNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavDestinations.SPLASH_ROUTE,
    modifier: Modifier = Modifier
) {
    val actions = remember(navController) { NavActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavDestinations.SPLASH_ROUTE) {
            SplashScreen(
                onNavigateToLogin = actions.navigateToLogin,
                onNavigateToHome = actions.navigateToHome
            )
        }

        composable(NavDestinations.LOGIN_ROUTE) {
            LoginScreen(
                onNavigateToRegister = actions.navigateToRegister,
                onNavigateToHome = actions.navigateToHome
            )
        }

        composable(NavDestinations.REGISTER_ROUTE) {
            RegisterScreen(
                onNavigateToHome = actions.navigateToHome,
                onNavigateBack = actions.navigateBack
            )
        }

        composable(NavDestinations.HOME_ROUTE) {
            HomeScreen(
                onNavigateToListings = actions.navigateToListings,
                onNavigateToFinancial = actions.navigateToFinancial,
                onNavigateToMarket = actions.navigateToMarket
            )
        }

        composable(NavDestinations.LISTINGS_ROUTE) {
            ListingsScreen(
                onNavigateToHome = actions.navigateToHome,
                onNavigateToCreateListing = actions.navigateToCreateListing,
                onNavigateToProfile = actions.navigateToProfile,
                onNavigateToListingDetail = actions.navigateToListingDetail
            )
        }

        composable(
            route = "${NavDestinations.LISTING_DETAIL_ROUTE}/{${NavDestinations.LISTING_ID_ARG}}",
            arguments = listOf(navArgument(NavDestinations.LISTING_ID_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val listingId =
                backStackEntry.arguments?.getString(NavDestinations.LISTING_ID_ARG) ?: ""
            ListingDetailScreen(
                listingId = listingId,
                onNavigateBack = actions.navigateBack
            )
        }

        composable(NavDestinations.CREATE_LISTING_ROUTE) {
            CreateListingScreen(
                onNavigateBack = actions.navigateBack
            )
        }

        composable(NavDestinations.PROFILE_ROUTE) {
            ProfileScreen(
                onNavigateBack = actions.navigateBack,
                onNavigateToLogin = actions.navigateToLoginFromLogout,
                onNavigateToEditProfile = actions.navigateToEditProfile
            )
        }

        composable(NavDestinations.EDIT_PROFILE_ROUTE) {
            EditProfileScreen(
                onNavigateBack = actions.navigateBack
            )
        }

        composable(NavDestinations.FINANCIAL_ROUTE) {
            FinancialScreen(
                onNavigateBack = actions.navigateBack,
                onNavigateToReports = actions.navigateToReports
            )
        }

        composable(NavDestinations.REPORTS_ROUTE) {
            ReportsScreen(
                onNavigateBack = actions.navigateBack
            )
        }

        composable(NavDestinations.MARKET_ROUTE) {
            MarketScreen(
                onNavigateBack = actions.navigateBack
            )
        }
    }
}

class NavActions(private val navController: NavHostController) {
    val navigateToLogin: () -> Unit = {
        navController.navigate(NavDestinations.LOGIN_ROUTE) {
            popUpTo(NavDestinations.SPLASH_ROUTE) { inclusive = true }
        }
    }

    val navigateToRegister: () -> Unit = {
        navController.navigate(NavDestinations.REGISTER_ROUTE)
    }

    val navigateToHome: () -> Unit = {
        navController.navigate(NavDestinations.HOME_ROUTE) {
            popUpTo(NavDestinations.LOGIN_ROUTE) { inclusive = true }
        }
    }

    val navigateToListings: () -> Unit = {
        navController.navigate(NavDestinations.LISTINGS_ROUTE)
    }

    val navigateToListingDetail: (String) -> Unit = { listingId ->
        navController.navigate("${NavDestinations.LISTING_DETAIL_ROUTE}/$listingId")
    }

    val navigateToCreateListing: () -> Unit = {
        navController.navigate(NavDestinations.CREATE_LISTING_ROUTE)
    }

    val navigateToProfile: () -> Unit = {
        navController.navigate(NavDestinations.PROFILE_ROUTE)
    }

    val navigateToEditProfile: () -> Unit = {
        navController.navigate(NavDestinations.EDIT_PROFILE_ROUTE)
    }

    val navigateToFinancial: () -> Unit = {
        navController.navigate(NavDestinations.FINANCIAL_ROUTE)
    }

    val navigateToReports: () -> Unit = {
        navController.navigate(NavDestinations.REPORTS_ROUTE)
    }

    val navigateToMarket: () -> Unit = {
        navController.navigate(NavDestinations.MARKET_ROUTE)
    }

    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }

    val navigateToLoginFromLogout: () -> Unit = {
        navController.navigate(NavDestinations.LOGIN_ROUTE) {

            popUpTo(0) { inclusive = true }
        }
    }
} 