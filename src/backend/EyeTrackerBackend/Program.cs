using System;
using System.IO;
using System.Diagnostics;
using System.Net.Sockets;
using System.Threading;
using Tobii.Interaction;

namespace EyeTrackerBackend
{
    class EyeTrackerBackend
    {
        private static TcpClient _client;

        private static class Constants
        {
            public const string Address = "127.0.0.1";
            public const int Port = 1234;
            public const int Max_Attempts = 100000;
        }

        public static void Main()
        {
            InitClient();
            var host = new Host();
            var gazePointDataStream = host.Streams.CreateGazePointDataStream();
            gazePointDataStream.GazePoint(TransmitData);
            Console.ReadKey();
            host.DisableConnection();
            _client.GetStream().Close();
            _client.Close();
        }

        private static void InitClient()
        {
            int attempts = 0;
            while (attempts < Constants.Max_Attempts)
            {
                try
                {
                    _client = new TcpClient(Constants.Address, Constants.Port);
                    Console.WriteLine("Connected to {0}:{1}. Transmitting Data....", Constants.Address, Constants.Port);
                    return;
                }
                catch (SocketException)

                {
                    Console.WriteLine("Failed to connect to {0}:{1} (attempt: {2}/{3})", Constants.Address, Constants.Port, ++attempts, Constants.Max_Attempts);
                    Thread.Sleep(2000);
                }
            }
            Debug.WriteLine("Maximum attempts reached. Exiting....");
            Environment.Exit(1);
        }

        private static void TransmitData(double x, double y, double t)
        {
            try
            {
                string message = $"{(int)Math.Round(x)}:{(int)Math.Round(y)}\n";
                Byte[] data = System.Text.Encoding.ASCII.GetBytes(message);
                _client.GetStream().Write(data, 0, data.Length);
            }
            catch (SocketException e)
            {
                Debug.WriteLine("SocketException: {0}", e);
                InitClient();
            } catch (IOException e)
            {
                Debug.WriteLine("IOException: {0}", e);
                InitClient();
            }
    
        }
    }
}