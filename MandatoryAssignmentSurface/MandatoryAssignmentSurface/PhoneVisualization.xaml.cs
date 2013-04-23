using System;
using System.Collections.Generic;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using Microsoft.Surface;
using Microsoft.Surface.Presentation;
using Microsoft.Surface.Presentation.Controls;
using Microsoft.Surface.Presentation.Input;

namespace MandatoryAssignmentSurface
{
    /// <summary>
    /// Interaction logic for PhoneVisualization.xaml
    /// </summary>
    public partial class PhoneVisualization : TagVisualization
    {
        public PhoneVisualization()
        {
            InitializeComponent();
        }

        private void PhoneVisualization_Loaded(object sender, RoutedEventArgs e)
        {
            //TODO: customize PhoneVisualization's UI based on this.VisualizedTag here
        }

        private void TagVisualization_DragOver(object sender, DragEventArgs e)
        {
            Console.WriteLine("TagVisualization drag over");
        }

        private void surfaceButton1_Click(object sender, RoutedEventArgs e)
        {
            Console.WriteLine("Pin button pressed");
        }

        private void surfaceButton1_Click_1(object sender, RoutedEventArgs e)
        {

        }
    }
}
