import React from 'react';
import { Sun, Moon } from 'lucide-react';
import { useTheme } from '../contexts/ThemeContext';

/**
 * Theme Toggle Button component for switching between dark and light modes
 * 
 * Displays a sun icon for dark mode and moon icon for light mode
 * Includes smooth transitions and hover effects
 * 
 * @author Aircraft Monitoring Team
 * @version 1.0.0
 */
const ThemeToggle = () => {
  const { isDarkMode, toggleTheme } = useTheme();

  return (
    <button
      onClick={toggleTheme}
      className={`
        relative flex items-center justify-center w-10 h-10 rounded-lg
        transition-all duration-200 ease-in-out
        ${isDarkMode 
          ? 'bg-gray-700 hover:bg-gray-600 text-yellow-400' 
          : 'bg-gray-200 hover:bg-gray-300 text-gray-800'
        }
        focus:outline-none focus:ring-2 focus:ring-aviation-blue focus:ring-offset-2
        ${isDarkMode ? 'focus:ring-offset-gray-800' : 'focus:ring-offset-white'}
      `}
      title={isDarkMode ? 'Switch to light mode' : 'Switch to dark mode'}
      aria-label={isDarkMode ? 'Switch to light mode' : 'Switch to dark mode'}
    >
      {isDarkMode ? (
        <Sun className="w-5 h-5 transform transition-transform duration-200 hover:rotate-12" />
      ) : (
        <Moon className="w-5 h-5 transform transition-transform duration-200 hover:-rotate-12" />
      )}
    </button>
  );
};

export default ThemeToggle;